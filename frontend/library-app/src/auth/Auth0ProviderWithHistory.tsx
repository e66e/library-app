import { useNavigate } from "react-router-dom";
import { Auth0Provider } from "@auth0/auth0-react";
import { auth0Config } from "../lib/auth0Config";

const Auth0ProviderWithHistory = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const navigate = useNavigate();

  const onRedirectCallback = (appState: any) => {
    console.log(appState?.returnTo || "not found");
    navigate(appState?.returnTo || "/");
  };

  return (
    <Auth0Provider
      domain={auth0Config.issuer}
      clientId={auth0Config.clientId}
      authorizationParams={{
        redirect_uri: auth0Config.redirectUri,
        audience: auth0Config.audience,
        scope: auth0Config.scope,
      }}
      onRedirectCallback={onRedirectCallback}
      cacheLocation="localstorage"
    >
      {children}
    </Auth0Provider>
  );
};

export default Auth0ProviderWithHistory;
