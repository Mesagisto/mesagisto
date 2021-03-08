use std::io::{BufRead, BufReader};

use color_eyre::eyre::Result;

use crate::config::CONFIG;

pub async fn read_certs_from_file() -> Result<(Vec<rustls::Certificate>, rustls::PrivateKey)> {
  let cert_file = std::fs::File::open(CONFIG.tls.cert.as_str())?;
  let mut cert_chain_reader = BufReader::new(cert_file);
  let certs = rustls_pemfile::certs(&mut cert_chain_reader)?
    .into_iter()
    .map(rustls::Certificate)
    .collect::<Vec<_>>();
  assert!(!certs.is_empty());
  let key_file = std::fs::File::open(CONFIG.tls.key.as_str())?;
  let mut key_reader = BufReader::new(key_file);
  let mut head = String::new();
  key_reader.read_line(&mut head)?;
  let key_file = std::fs::File::open(CONFIG.tls.key.as_str())?;
  let mut key_reader = BufReader::new(key_file);
  let mut keys = if head.contains("BEGIN RSA PRIVATE KEY") {
    rustls_pemfile::rsa_private_keys(&mut key_reader)?
  } else if head.contains("BEGIN PRIVATE KEY") {
    rustls_pemfile::pkcs8_private_keys(&mut key_reader)?
  } else {
    panic!("Unknown key format")
  };
  assert_eq!(keys.len(), 1);
  let key = rustls::PrivateKey(keys.remove(0));

  Ok((certs, key))
}

#[test]
#[cfg(feature = "dev")]
fn gen() -> Result<()> {
  use rcgen::generate_simple_self_signed;
  let subject_alt_names = vec!["hello.world.example".to_string(), "localhost".to_string()];
  let cert = generate_simple_self_signed(subject_alt_names).unwrap();
  // The certificate is now valid for localhost and the domain
  // "hello.world.example"
  std::fs::write("./res/server-cert.pem", cert.serialize_pem().unwrap())?;
  std::fs::write("./res/server-key.pem", cert.serialize_private_key_pem())?;
  Ok(())
}
