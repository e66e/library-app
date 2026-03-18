export function deleteCookiesByNameContains(
  substring: string,
  domain: string = "",
  path: string = "/",
) {
  // Get all cookies as an array of strings: ["name=value", ...]
  const cookies = document.cookie.split("; ");

  cookies.forEach((cookie) => {
    // Extract the cookie name (everything before the first '=')
    const eqPos = cookie.indexOf("=");
    const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;

    // If the name contains the substring, delete this cookie
    if (name.includes(substring)) {
      // Build the expiration string for a past date
      const expires = "expires=Thu, 01 Jan 1970 00:00:00 UTC";

      // Build the cookie string to delete. Must include the same path and domain.
      let cookieString = `${name}=; ${expires}; path=${path}`;
      if (domain) {
        cookieString += `; domain=${domain}`;
      }

      // Apply the deletion by setting the cookie with an expired date
      document.cookie = cookieString;
    }
  });
}

export function cleanAuth0Keys() {
  const keysToRemove = [];
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (!key) {
      continue;
    }
    if (key.startsWith("@@auth0spajs@@")) {
      keysToRemove.push(key);
    }
  }
  keysToRemove.forEach((key) => localStorage.removeItem(key));
}
