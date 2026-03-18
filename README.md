In frontend remember to create auth0Config.ts file like this:
```

export const auth0Config = {
  clientId: "foo-clientId", // clientId from auth0 
  issuer: "foo-issuerl", // auth0 frontend app issuer
  audience: "https://localhost:8443",
  redirectUri: window.location.origin,
  scope: "openid profile email",
};

```
