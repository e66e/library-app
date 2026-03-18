import { useAuth0 } from "@auth0/auth0-react";
import { useEffect } from "react";
import { Navigate, Outlet } from "react-router-dom";

const ProtectedRoute = () => {
  const { isAuthenticated, loginWithRedirect, isLoading } = useAuth0();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      loginWithRedirect({
        appState: {
          returnTo: window.location.pathname,
        },
      });
    }
  }, [isAuthenticated, isLoading]);

  return isAuthenticated ? <Outlet /> : null;
};

export default ProtectedRoute;
