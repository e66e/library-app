Implementation of "Full Stack: React and Java Spring Boot - The Developer Guide (https://www.udemy.com/course/full-stack-react-and-java-spring-boot-the-developer-guide/)" with my own changes to it.

In frontend(src/lib/) remember to create auth0Config.ts file like this:
```

export const auth0Config = {
  clientId: "foo-clientId", // clientId from auth0 
  issuer: "foo-issuerl", // auth0 frontend app issuer
  audience: "https://localhost:8443",
  redirectUri: window.location.origin,
  scope: "openid profile email",
};

```
