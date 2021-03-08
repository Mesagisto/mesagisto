use std::{
  str::FromStr,
  sync::atomic::{AtomicU8, Ordering},
};

use color_eyre::eyre;
use rand::Rng;
use steven_protocol::protocol::{self, forge, mojang, packet, packet::Packet};
use tracing::{debug, info, trace, warn};

pub(crate) const TARGET: &str = "mesagisto::game";
pub(crate) const STEP: AtomicU8 = AtomicU8::new(0);

#[derive(Debug)]
pub struct Client {
  // 340 1.12.2
  pub default_protocol_version: i32,
  pub profile: protocol::mojang::Profile,
}

impl Client {
  pub fn new(default_protocol_version: i32, profile: protocol::mojang::Profile) -> Self {
    Self {
      default_protocol_version,
      profile,
    }
  }

  pub async fn connect_to(&self, address: &str) -> eyre::Result<Server> {
    let (protocol_version, forge_mods, fml_network_version) =
      match protocol::Conn::new(address, 340).and_then(|conn| conn.do_status()) {
        Ok(res) => {
          info!(
            target: TARGET,
            "Detected server protocol version {}", res.0.version.protocol
          );
          (
            res.0.version.protocol,
            res.0.forge_mods,
            res.0.fml_network_version,
          )
        }
        Err(err) => {
          warn!(
            target: TARGET,
            "Error pinging server {} to get protocol version: {:?}, fallback to {}",
            address,
            err,
            self.default_protocol_version
          );
          (self.default_protocol_version, vec![], None)
        }
      };

    let address = address.to_owned();
    let server = Server::connect(
      &self.profile,
      &address,
      protocol_version,
      forge_mods,
      fml_network_version,
    )
    .await?;
    Ok(server)
  }
}

pub struct Server {
  pub uuid: protocol::UUID,
  pub conn: Option<protocol::Conn>,
  pub read_queue:
    Option<tokio::sync::mpsc::UnboundedReceiver<Result<packet::Packet, protocol::Error>>>,
}
impl Server {
  pub async fn connect(
    profile: &mojang::Profile,
    address: &str,
    protocol_version: i32,
    forge_mods: Vec<forge::ForgeMod>,
    fml_network_version: Option<i64>,
  ) -> Result<Server, protocol::Error> {
    let mut conn = protocol::Conn::new(address, protocol_version)?;

    let tag = match fml_network_version {
      Some(1) => "\0FML\0",
      Some(2) => "\0FML2\0",
      None => "",
      _ => panic!("unsupported FML network version: {:?}", fml_network_version),
    };

    let host = conn.host.clone() + tag;
    let port = conn.port;

    trace!("writing pkt");
    conn.write_packet(protocol::packet::handshake::serverbound::Handshake {
      protocol_version: protocol::VarInt(protocol_version),
      host,
      port,
      next: protocol::VarInt(2),
    })?;
    trace!("writing pkt success");
    conn.state = protocol::State::Login;
    conn.write_packet(protocol::packet::login::serverbound::LoginStart {
      username: profile.username.clone(),
    })?;

    use std::rc::Rc;
    let (server_id, public_key, verify_token);
    loop {
      match conn.read_packet()? {
        protocol::packet::Packet::SetInitialCompression(val) => {
          conn.set_compresssion(val.threshold.0);
        }
        Packet::EncryptionRequest(val) => {
          server_id = Rc::new(val.server_id);
          public_key = Rc::new(val.public_key.data);
          verify_token = Rc::new(val.verify_token.data);
          break;
        }
        Packet::EncryptionRequest_i16(val) => {
          server_id = Rc::new(val.server_id);
          public_key = Rc::new(val.public_key.data);
          verify_token = Rc::new(val.verify_token.data);
          break;
        }
        Packet::LoginSuccess_String(val) => {
          warn!(target: TARGET, "Server is running in offline mode");
          debug!(target: TARGET, "Login: {} {}", val.username, val.uuid);
          let mut read = conn.clone();
          let mut write = conn;
          read.state = protocol::State::Play;
          write.state = protocol::State::Play;
          let rx = Self::spawn_reader_async(read);
          return Ok(Server::new(
            protocol_version,
            forge_mods,
            protocol::UUID::from_str(&val.uuid).unwrap(),
            Some(write),
            Some(rx),
          ));
        }
        Packet::LoginSuccess_UUID(val) => {
          warn!(target: TARGET, "Server is running in offline mode");
          debug!(target: TARGET, "Login: {} {:?}", val.username, val.uuid);
          let mut read = conn.clone();
          let mut write = conn;
          read.state = protocol::State::Play;
          write.state = protocol::State::Play;
          let rx = Self::spawn_reader_async(read);
          return Ok(Server::new(
            protocol_version,
            forge_mods,
            val.uuid,
            Some(write),
            Some(rx),
          ));
        }
        Packet::LoginDisconnect(val) => {
          return Err(protocol::Error::Disconnect(val.reason));
        }
        val => return Err(protocol::Error::Err(format!("Wrong packet 1: {:?}", val))),
      };
    }

    let mut shared = [0; 16];
    rand::thread_rng().fill(&mut shared);

    let shared_e = rsa_public_encrypt_pkcs1::encrypt(&public_key, &shared).unwrap();
    let token_e = rsa_public_encrypt_pkcs1::encrypt(&public_key, &verify_token).unwrap();

    #[cfg(not(target_arch = "wasm32"))]
    {
      profile
        .join_server(&server_id, &shared, &public_key)
        .await?;
    }

    if protocol_version >= 47 {
      conn.write_packet(protocol::packet::login::serverbound::EncryptionResponse {
        shared_secret: protocol::LenPrefixedBytes::new(shared_e),
        verify_token: protocol::LenPrefixedBytes::new(token_e),
      })?;
    } else {
      conn.write_packet(
        protocol::packet::login::serverbound::EncryptionResponse_i16 {
          shared_secret: protocol::LenPrefixedBytes::new(shared_e),
          verify_token: protocol::LenPrefixedBytes::new(token_e),
        },
      )?;
    }

    let mut read = conn.clone();
    let mut write = conn;

    read.enable_encyption(&shared, true);
    write.enable_encyption(&shared, false);

    let uuid;
    let compression_threshold = read.compression_threshold;
    loop {
      match read.read_packet()? {
        Packet::SetInitialCompression(val) => {
          assert!(STEP.fetch_max(8, Ordering::Relaxed) < 8);
          trace!(
            target: TARGET,
            "step8 S->C Set Compression (Optional, enables compression)"
          );
          read.set_compresssion(val.threshold.0);
          write.set_compresssion(val.threshold.0);
        }
        Packet::LoginSuccess_String(val) => {
          assert!(STEP.fetch_max(9, Ordering::Relaxed) < 9);
          trace!(target: TARGET, "step9 S->C Login Success");
          debug!(target: TARGET, "Login: {} {}", val.username, val.uuid);
          uuid = protocol::UUID::from_str(&val.uuid).unwrap();
          read.state = protocol::State::Play;
          write.state = protocol::State::Play;
          break;
        }
        Packet::LoginSuccess_UUID(val) => {
          assert!(STEP.fetch_max(9, Ordering::Relaxed) < 9);
          trace!(target: TARGET, "step9 S->C Login Success");
          debug!(target: TARGET, "Login: {} {:?}", val.username, val.uuid);
          uuid = val.uuid;
          read.state = protocol::State::Play;
          write.state = protocol::State::Play;
          break;
        }
        Packet::LoginDisconnect(val) => {
          return Err(protocol::Error::Disconnect(val.reason));
        }
        Packet::LoginPluginRequest(req) => match req.channel.as_ref() {
          "fml:loginwrapper" => {
            let mut cursor = std::io::Cursor::new(req.data);
            let channel: String = protocol::Serializable::read_from(&mut cursor)?;

            let (id, mut data) =
              protocol::Conn::read_raw_packet_from(&mut cursor, compression_threshold)?;

            match channel.as_ref() {
              "fml:handshake" => {
                let packet = forge::fml2::FmlHandshake::packet_by_id(id, &mut data)?;
                use forge::fml2::FmlHandshake::*;
                match packet {
                  ModList {
                    mod_names,
                    channels,
                    registries,
                  } => {
                    info!(
                      target: TARGET,
                      "ModList mod_names={:?} channels={:?} registries={:?}",
                      mod_names,
                      channels,
                      registries
                    );
                    write.write_fml2_handshake_plugin_message(
                      req.message_id,
                      Some(&ModListReply {
                        mod_names,
                        channels,
                        registries,
                      }),
                    )?;
                  }
                  ServerRegistry {
                    name,
                    snapshot_present: _,
                    snapshot: _,
                  } => {
                    info!(target: TARGET, "ServerRegistry {:?}", name);
                    write.write_fml2_handshake_plugin_message(
                      req.message_id,
                      Some(&Acknowledgement),
                    )?;
                  }
                  ConfigurationData { filename, contents } => {
                    info!(
                      target: TARGET,
                      "ConfigurationData filename={:?} contents={}",
                      filename,
                      String::from_utf8_lossy(&contents)
                    );
                    write.write_fml2_handshake_plugin_message(
                      req.message_id,
                      Some(&Acknowledgement),
                    )?;
                  }
                  _ => unimplemented!(),
                }
              }
              _ => panic!(
                "unknown LoginPluginRequest fml:loginwrapper channel: {:?}",
                channel
              ),
            }
          }
          _ => panic!("unsupported LoginPluginRequest channel: {:?}", req.channel),
        },
        val => return Err(protocol::Error::Err(format!("Wrong packet 2: {:?}", val))),
      }
    }

    let rx = Self::spawn_reader_async(read);

    Ok(Server::new(
      protocol_version,
      forge_mods,
      uuid,
      Some(write),
      Some(rx),
    ))
  }

  fn spawn_reader_async(
    mut read: protocol::Conn,
  ) -> tokio::sync::mpsc::UnboundedReceiver<Result<packet::Packet, protocol::Error>> {
    let (tx, rx) = tokio::sync::mpsc::unbounded_channel();
    tokio::task::spawn_blocking(move || {
      loop {
        let pck = read.read_packet();
        let was_error = pck.is_err();

        if tx.send(pck).is_err() {
          return;
        }
        if was_error {
          return;
        }
      }
    });
    rx
  }

  fn new(
    _protocol_version: i32,
    _forge_mods: Vec<forge::ForgeMod>,
    uuid: protocol::UUID,
    conn: Option<protocol::Conn>,
    read_queue: Option<
      tokio::sync::mpsc::UnboundedReceiver<Result<packet::Packet, protocol::Error>>,
    >,
  ) -> Server {
    Server {
      uuid,
      conn,
      // protocol_version,
      // forge_mods,
      read_queue,
      // disconnect_reason: None,
    }
  }
}
