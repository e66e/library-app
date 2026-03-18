import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { routes } from "./Routes.tsx";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";

const router = createBrowserRouter(routes);

const stripePromise = loadStripe(
  "pk_test_51T1tzxCKbvgkF4esjOmoPOQCtKtM20piiGforkQeNpxZ5XnPUzNvJ9gv22ipC4Gm0gOqzs8i9YPPGAV9T1CSEejj007icJCAwk",
);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Elements stripe={stripePromise}>
      <RouterProvider router={router} />
    </Elements>
  </StrictMode>,
);
