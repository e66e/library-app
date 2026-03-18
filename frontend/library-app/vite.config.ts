import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import fs from "fs";
import path from "path";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    https: {
      key: fs.readFileSync(
        path.resolve(__dirname, "./ssl-localhost/localhost.key"),
      ),
      cert: fs.readFileSync(
        path.resolve(__dirname, "./ssl-localhost/localhost.crt"),
      ),
    },
  },
  // define: {
  //   VITE_REACT_APP_API: "https://127.0.0.1:8443/api",
  // },
});
