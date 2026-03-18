import { useAuth0 } from "@auth0/auth0-react";
import { cleanAuth0Keys, deleteCookiesByNameContains } from "../utils/Cleaner";

const LogoutButton = () => {
  const { logout } = useAuth0();

  const handleLogout = () => {
    console.log("handleLogout");
    logout({ logoutParams: { returnTo: window.location.origin } });
    cleanAuth0Keys();
    deleteCookiesByNameContains("auth");
  };

  return (
    <button className="btn btn-outline-light" onClick={handleLogout}>
      Logout
    </button>
  );
};

export default LogoutButton;
