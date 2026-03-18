import { Outlet } from "react-router-dom";
import Footer from "./layouts/NavbarAndFooter/Footer.tsx";
import Navbar from "./layouts/NavbarAndFooter/Navbar.tsx";

const App = () => {
  return (
    <div className="d-flex flex-column min-vh-100">
      <Navbar />
      <div className="flex-grow-1">
        <Outlet />
      </div>
      <Footer />
    </div>
  );
};

export default App;
