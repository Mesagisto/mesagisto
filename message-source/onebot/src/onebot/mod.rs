use std::collections::HashMap;

use arcstr::ArcStr;
use serde::{Deserialize, Serialize};
use serde_json::Value;

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct Request {
    // 请求 API 端点
    action: ArcStr,
    // 请求参数
    params: Value,
    // 回显字段
    echo: ArcStr,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct Response {
    // 状态，ok 为成功，其他将在下文中详细说明
    status: ArcStr,
    // 返回码，0 为成功，非 0 为失败
    retcode: u32,
    // 错误信息，仅在 API 调用失败时出现
    msg: ArcStr,
    // 对错误信息的描述，仅在 API 调用失败时出现
    wording: ArcStr,
    data: HashMap<ArcStr, Value>,
    // 回显字段
    echo: ArcStr,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Event {
    // 时间戳
    time: i64,
    // 机器人QQ
    self_id: i64,
    // 上报数据
    #[serde(flatten)]
    #[serde(rename = "post")]
    pub content: Post,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "post_type")]
pub enum Post {
    #[serde(rename = "message")]
    Message {
        message_type: ArcStr, // (消息类型)[https://whitechi73.github.io/OpenShamrock/event/general-data.html#messagetype] group/private
        sub_type: ArcStr, // (消息子类型)[https://whitechi73.github.io/OpenShamrock/event/general-data.html#messagesubtype] normal/friend/...
        message_id: i64,  // 消息 ID
        user_id: i64,     // 发送者 QQ 号
        message: Vec<MsgSegment>, // 消息内容
        raw_message: ArcStr, // CQ 码格式消息
        sender: Value,
        group_id: Option<i64>,    // 群号
        target_id: Option<i64>,   // 消息目标（私聊）
        temp_source: Option<i32>, // 临时聊天来源（私聊）
        // peer_id: i64,             // 消息接收者，群聊是群号，私聊时是目标QQ
        // font: i32, 已丢弃字段
        #[serde(flatten)]
        rest: HashMap<ArcStr, Value>, // 冗余字段
    },
    #[serde(rename = "message_sent")]
    MessageSent(HashMap<ArcStr, Value>),
    #[serde(rename = "notice")]
    Notice(HashMap<ArcStr, Value>),
    #[serde(rename = "request")]
    Request(HashMap<ArcStr, Value>),
    #[serde(rename = "meta_event")]
    Meta(MetaEvent),
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "meta_event_type")]
pub enum MetaEvent {
    #[serde(rename = "heartbeat")]
    HeartBeatEvent {
        internal: Option<i64>,
        status: Value,
    },
    #[serde(rename = "lifecycle")]
    LifecycleEvent { sub_type: ArcStr },
}

#[derive(Debug, Clone, Serialize, Deserialize)]
// Adjacently tagged
#[serde(tag = "type", content = "data")]
pub enum MsgSegment {
    #[serde(rename = "text")]
    Text { text: ArcStr },
    #[serde(rename = "face")]
    Face { id: ArcStr },
    #[serde(rename = "image")]
    Image {
        #[serde(rename = "file")]
        filename: ArcStr,
        #[serde(rename = "type")]
        ty: ArcStr,
        url: ArcStr,
    },
    #[serde(rename = "at")]
    At { qq: ArcStr },
    #[serde(rename = "poke")]
    Poke {
        #[serde(rename = "type")]
        ty: i32,
        id: i32,
        name: Option<ArcStr>,
    },
    #[serde(rename = "reply")]
    Reply { id: i32 },
}
