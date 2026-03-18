import { useAuth0 } from "@auth0/auth0-react";
import { cleanAuth0Keys } from "../utils/Cleaner";

interface Props {
  text?: string;
}

const SignInButton = ({ text = "Sign In" }: Props) => {
  const { loginWithRedirect } = useAuth0();

  const handleLogin = () => {
    cleanAuth0Keys();
    loginWithRedirect();
    window.location.assign("/");
  };

  return (
    <button className="btn btn-outline-light main-color" onClick={handleLogin}>
      {text}
    </button>
  );
};

export default SignInButton;
