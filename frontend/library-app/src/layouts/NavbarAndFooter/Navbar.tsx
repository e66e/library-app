import { useAuth0 } from "@auth0/auth0-react";
import { NavLink } from "react-router-dom";
import SignInButton from "../../auth/SignInButton";
import LogoutButton from "../../auth/LogoutButton";
import { decodeJwtPayload } from "../../utils/Decoders";
import { useEffect, useState } from "react";

const Navbar = () => {
  const { isAuthenticated, getAccessTokenSilently } = useAuth0();
  const [roles, setRoles] = useState<string[]>([]);

  // Printing JWT
  useEffect(() => {
    const getJWT = async () => {
      const token = await getAccessTokenSilently();
      console.log(token);

      const payload = decodeJwtPayload(token);
      setRoles(payload?.userRoles);
    };

    getJWT().catch((error: any) => {
      console.log(error.message);
    });
  }, [isAuthenticated]);

  return (
    <nav className="navbar navbar-expand-lg navbar-dark main-color py-3">
      <div className="container-fluid">
        <span className="navbar-brand">Luv 2 Read</span>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown"
          aria-expanded="false"
          aria-label="Toggle Navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNavDropdown">
          <ul className="navbar-nav">
            <li className="nav-item">
              <NavLink className="nav-link" to="/">
                Home
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/search">
                Search Books
              </NavLink>
            </li>
            {isAuthenticated && (
              <li className="nav-item">
                <NavLink className="nav-link" to="/shelf">
                  Shelf
                </NavLink>
              </li>
            )}
            {isAuthenticated && (
              <li className="nav-item">
                <NavLink className="nav-link" to="/fees">
                  Pay fees
                </NavLink>
              </li>
            )}
            {isAuthenticated && roles?.includes("admin") && (
              <li className="nav-item">
                <NavLink className="nav-link" to="/admin">
                  Admin
                </NavLink>
              </li>
            )}
          </ul>
          <ul className="navbar-nav ms-auto">
            {!isAuthenticated ? (
              <li className="nav-item m-1">
                <SignInButton />
              </li>
            ) : (
              <li>
                <LogoutButton />
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
