#![feature(async_closure)]

mod command;
mod config;
mod handler;
mod log;
pub mod onebot;

use crate::onebot::Event;
use color_eyre::eyre;
use futures_util::{SinkExt, StreamExt};
use mesagisto_client::ResultExt;
use onebot::{MetaEvent, Post};
use serde_json::Value;
use tokio::net::{TcpListener, TcpStream};
use tokio_tungstenite::tungstenite::{
    handshake::server::{Callback, ErrorResponse, Request, Response},
    Message,
};

#[macro_use]
extern crate educe;

#[tokio::main]
async fn main() -> eyre::Result<()> {
    if cfg!(feature = "color") {
        color_eyre::install()?;
    } else {
        color_eyre::config::HookBuilder::new()
            .theme(color_eyre::config::Theme::new())
            .install()?;
    }
    self::log::init().await?;

    // let (mut stream,response) = tokio_tungstenite::connect_async("ws://127.0.0.1:3001/").await?;
    // dbg!(response);

    // let (tx,rx)  = stream.split();
    // tokio::spawn(async {
    //     rx.for_each(|message| async {
    //         dbg!(message.unwrap());
    //     })
    // });


    let listener = TcpListener::bind("0.0.0.0:2456").await?;
    tracing::info!("WebSocket服务器已建立");
    while let Ok((stream, _)) = listener.accept().await {
        tokio::spawn(async {
            accept_connection(stream).await.unwrap();
        });
    }
    tokio::signal::ctrl_c().await?;
    Ok(())
}

async fn accept_connection(stream: TcpStream) -> eyre::Result<()> {
    let addr = stream
        .peer_addr()
        .expect("connected streams should have a peer address");
    #[derive(Clone, Copy, Debug)]
    pub struct MyCallback;

    impl Callback for MyCallback {
        fn on_request(
            self,
            request: &Request,
            response: Response,
        ) -> Result<Response, ErrorResponse> {
            tracing::trace!("WebSocket 握手头部信息:\n{:#?}", request);
            Ok(response)
        }
    }

    let ws_stream = tokio_tungstenite::accept_hdr_async(stream, MyCallback)
        .await
        .expect("Error during the websocket handshake occurred");

    tracing::trace!("新 WebSocket 连接已建立，对端: {}", addr);

    let (mut write, mut read) = ws_stream.split();

    let (tx, mut rx) = tokio::sync::mpsc::channel::<Message>(128);
    write.send(Message::Ping(Vec::new())).await?;
    tokio::spawn(async move {
        while let Some(frame) = rx.recv().await {
            write.send(frame).await.unwrap();
        }
        tracing::trace!("WebSocket 帧发送channel 已终止");
    });

    while let Some(frame) = read.next().await {
        match frame {
            Ok(Message::Text(frame)) => {
                if let Some(event) = serde_json::from_str::<Event>(&frame).log() {
                    match event.content {
                        Post::Meta(MetaEvent::HeartBeatEvent { internal:_, status:_ }) => {
                            // Do nothing
                        }
                        _ => {
                            tracing::trace!("{:#?}", event);
                        }
                    };
                } else {
                    let msg: Value = serde_json::from_str(&frame)?;
                    tracing::trace!("{:#?}", msg);
                }
            }
            Ok(Message::Ping(frame)) => {
                tracing::trace!("Received PING Frame, sending corresponding PONG Frame");
                tx.send(Message::Pong(frame)).await?;
            }
            Ok(Message::Close(_)) => {
                break;
            }
            Err(err) => {
                tracing::warn!("Websocket 连接已丢失 {}", err);
                break;
            }
            _ => {}
        }
    }

    Ok(())
}
