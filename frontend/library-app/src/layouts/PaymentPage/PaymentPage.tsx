import { useAuth0 } from "@auth0/auth0-react";
import { useEffect, useState } from "react";
import SpinnerLoading from "../Utils/SpinnerLoading";
import { decodeJwtPayload } from "../../utils/Decoders";
import { CardElement, useElements, useStripe } from "@stripe/react-stripe-js";
import { Link } from "react-router-dom";
import PaymentInfo from "../../models/PaymentInfo";

const PaymentPage = () => {
  const { isAuthenticated, getAccessTokenSilently } = useAuth0();
  const [httpError, setHttpError] = useState<boolean>(false);
  const [submitDisabled, setSubmitDisabled] = useState<boolean>(false);
  const [fees, setFees] = useState(0);
  const [loadingFees, setLoadingFees] = useState<boolean>(true);

  useEffect(() => {
    const fetchFees = async () => {
      if (isAuthenticated) {
        const token = await getAccessTokenSilently();
        const payload = decodeJwtPayload(token);
        const email = payload?.email;
        const url = `${import.meta.env.VITE_REACT_APP_API}/payments/search/findByUserEmail?userEmail=${email}`;
        const requestOptions = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        };
        const paymentResponse = await fetch(url, requestOptions);

        if (!paymentResponse.ok) {
          throw new Error("Something went wrong with fetching fees.");
        }
        const paymentResponseJson = await paymentResponse.json();
        setFees(paymentResponseJson.amount);
        setLoadingFees(false);
      }
    };

    fetchFees().catch((error: any) => {
      setLoadingFees(false);
      setHttpError(error);
    });
  }, [isAuthenticated]);

  const elements = useElements();
  const stripe = useStripe();

  async function checkout() {
    if (!stripe || !elements || !elements.getElement(CardElement)) {
      return;
    }

    setSubmitDisabled(true);

    const token = await getAccessTokenSilently();
    const payload = decodeJwtPayload(token);
    const email = payload?.email;

    let paymentInfo = new PaymentInfo(Math.round(fees * 100), "PLN", email);

    const url = `${import.meta.env.VITE_REACT_APP_API}/payments/secure/payment-intent`;
    const requestOptions = {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(paymentInfo),
    };
    const stripeResponse = await fetch(url, requestOptions);
    if (!stripeResponse.ok) {
      setHttpError(true);
      setSubmitDisabled(false);
      throw new Error("Something went wrong with fee checkout.");
    }
    const stripeResponseJson = await stripeResponse.json();

    stripe
      .confirmCardPayment(
        stripeResponseJson.client_secret,
        {
          payment_method: {
            card: elements.getElement(CardElement)!,
            billing_details: {
              email: email,
            },
          },
        },
        { handleActions: false },
      )
      .then(async function (resolve: any) {
        if (resolve.error) {
          setSubmitDisabled(false);
          alert("There was an error!");
        } else {
          const url = `${import.meta.env.VITE_REACT_APP_API}/payments/secure/payment-complete`;
          const requestOptions = {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          };
          const stripeResponse = await fetch(url, requestOptions);
          if (!stripeResponse.ok) {
            setHttpError(true);
            setSubmitDisabled(false);
            throw new Error("Something went wrong!");
          }
          setFees(0);
          setSubmitDisabled(false);
        }
      });
    setHttpError(false);
  }

  if (loadingFees) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  return (
    <div className="container">
      {fees > 0 && (
        <div className="card mt-3">
          <h5 className="card-header">
            Fees pending: <span className="text-danger">${fees}</span>
          </h5>
          <div className="card-body">
            <h5 className="card-title mb-3">Credit Card</h5>
            <CardElement id="card-element" />
            <button
              onClick={checkout}
              disabled={submitDisabled}
              type="button"
              className="btn btn-md main-color text-white mt-3"
            >
              Pay fees
            </button>
          </div>
        </div>
      )}
      {fees === 0 && (
        <div className="mt-3">
          <h5>You have no fees!</h5>
          <Link
            type="button"
            className="btn main-color text-white"
            to="/search"
          >
            Explore top books
          </Link>
        </div>
      )}
      {submitDisabled && <SpinnerLoading />}
    </div>
  );
};

export default PaymentPage;
