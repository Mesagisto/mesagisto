// #[test]
async fn test_auth_service() {
  use std::sync::Arc;

  use steven_protocol::protocol::mojang::{AuthService, Profile};
  let auth_service = Arc::new(AuthService::new(
    reqwest::Url::parse("https://skin.zengarden.top/api/yggdrasil/authserver/").unwrap(),
    reqwest::Url::parse("https://skin.zengarden.top/api/yggdrasil/sessionserver/").unwrap(),
  ));
  let profile = Profile::login_with_auth(
    "test@example.moe",
    "qazwsxedc123",
    uuid::Uuid::new_v4().as_u128().to_string().as_str(),
    auth_service,
  )
  .await
  .unwrap();
  dbg!(profile);
}
