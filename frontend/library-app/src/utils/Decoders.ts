export function base64ToUtf8(base64: string) {
  // Step 1: decode Base64 → binary string
  const binaryString = atob(base64);

  // Step 2: binary string → Uint8Array
  const bytes = new Uint8Array(binaryString.length);
  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }

  // Step 3: decode bytes as UTF-8
  return new TextDecoder("utf-8").decode(bytes);
}

export function base64UrlToBase64(base64Url: string) {
  // Replace URL-safe characters and add padding
  let base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  while (base64.length % 4) {
    base64 += "=";
  }
  return base64;
}

export function decodeJwtPayload(token: string) {
  try {
    // JWT format: header.payload.signature
    const parts = token.split(".");
    if (parts.length !== 3) {
      throw new Error("Invalid JWT format");
    }
    const payloadBase64Url = parts[1]; // the payload is the second part
    const payloadBase64 = base64UrlToBase64(payloadBase64Url);

    // Decode Base64 to binary string, then to UTF-8
    const binaryString = atob(payloadBase64);
    const bytes = new Uint8Array(binaryString.length);
    for (let i = 0; i < binaryString.length; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    const utf8String = new TextDecoder("utf-8").decode(bytes);

    // Parse JSON
    return JSON.parse(utf8String);
  } catch (error) {
    console.error("Failed to decode JWT payload:", error);
    return null;
  }
}
