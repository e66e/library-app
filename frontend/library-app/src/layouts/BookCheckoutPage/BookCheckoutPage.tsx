import { useEffect, useState } from "react";
import type BookModel from "../../models/BookModel";
import { useParams } from "react-router-dom";
import SpinnerLoading from "../Utils/SpinnerLoading";
import DefaultImg from "./../../assets/BooksImages/book-luv2code-1000.png";
import StarsReview from "../Utils/StarsReview";
import CheckoutAndReviewBox from "./CheckoutAndReviewBox";
import ReviewModel from "../../models/ReviewModel";
import LatestReviews from "./LatestReviews";
import { useAuth0 } from "@auth0/auth0-react";
import ReviewRequestModel from "../../models/ReviewRequestModel";

const BookCheckoutPage = () => {
  const { isAuthenticated, getAccessTokenSilently } = useAuth0();

  const [book, setBook] = useState<BookModel>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [httpError, setHttpError] = useState(null);

  // Review State
  const [reviews, setReviews] = useState<ReviewModel[]>([]);
  const [totalStars, setTotalStars] = useState<number>(0);
  const [isLoadingReview, setIsLoadingReview] = useState<boolean>(true);

  const [isReviewLeft, setIsReviewLeft] = useState(false);
  const [isLoadingUserReview, setIsLoadingUserReview] = useState(true);

  // Loans Count State
  const [currentLoansCount, setCurrentLoansCount] = useState(0);
  const [isLoadingCurrentLoansCount, setIsLoadingCurrentLoansCount] =
    useState(true);

  // Is Book Checked Out?
  const [isCheckedOut, setIsCheckedOut] = useState<Boolean>(false);
  const [isLoadingBookCheckOut, setIsLoadingBookCheckOut] = useState(true);

  // Payment
  const [displayError, setDisplayError] = useState<boolean>(false);

  const bookId = useParams<{ bookId: string }>().bookId;

  // Fetching info about certain book
  useEffect(() => {
    const fetchBook = async () => {
      const baseUrl: string = `${import.meta.env.VITE_REACT_APP_API}/books/${bookId}`;

      const response = await fetch(baseUrl);

      if (!response.ok) {
        throw new Error(
          "Something went wrong with fetching book in BookCheckoutPage",
        );
      }

      const responseJson = await response.json();

      const loadedBook: BookModel = {
        id: responseJson.id,
        title: responseJson.title,
        author: responseJson.author,
        description: responseJson.description,
        copies: responseJson.copies,
        copiesAvailable: responseJson.copiesAvailable,
        category: responseJson.category,
        img: responseJson.img,
      };

      setBook(loadedBook);
      setIsLoading(false);
    };

    fetchBook().catch((error: any) => {
      setHttpError(error);
      setIsLoading(false);
    });
  }, [isCheckedOut]);

  // Fetching reviews for a certain book
  useEffect(() => {
    const fetchBookReview = async () => {
      const reviewUrl: string = `${import.meta.env.VITE_REACT_APP_API}/reviews/search/findBookById?bookId=${bookId}`;

      const responseReviews = await fetch(reviewUrl);

      if (!responseReviews.ok) {
        throw new Error("Something went wrong with fetching book review");
      }

      const responseJsonReviews = await responseReviews.json();

      const responseData = responseJsonReviews.content;

      const loadedReviews: ReviewModel[] = [];

      let weightedStarReviews: number = 0;

      for (const key in responseData) {
        loadedReviews.push({
          id: responseData[key].id,
          userEmail: responseData[key].userEmail,
          date: responseData[key].date,
          rating: responseData[key].rating,
          bookId: responseData[key].bookId,
          reviewDescription: responseData[key].reviewDescription,
        });
        weightedStarReviews = weightedStarReviews + responseData[key].rating;
      }

      if (loadedReviews) {
        const round: string = (
          Math.round((weightedStarReviews / loadedReviews.length) * 2) / 2
        ).toFixed(1);
        setTotalStars(Number(round));
      }

      setReviews(loadedReviews);
      setIsLoadingReview(false);
    };

    fetchBookReview().catch((error: any) => {
      setIsLoading(false);
      setHttpError(error);
    });
  }, [isReviewLeft]);

  useEffect(() => {
    const fetchUserReviewBook = async () => {
      if (isAuthenticated) {
        const accessToken = await getAccessTokenSilently();
        const url = `${import.meta.env.VITE_REACT_APP_API}/reviews/secure/user/book?bookId=${bookId}`;
        const requestOptions = {
          method: "GET",
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const userReview = await fetch(url, requestOptions);
        if (!userReview.ok) {
          throw new Error(
            "Something went wrong with fetching users' reviews on a book.",
          );
        }
        const userReviewResponseJson = await userReview.json();
        setIsReviewLeft(userReviewResponseJson);
      }

      setIsLoadingUserReview(false);
    };

    fetchUserReviewBook().catch((error: any) => {
      setIsLoadingUserReview(false);
      setHttpError(error.message);
    });
  }, [isAuthenticated]);

  // Fetching how many books user loans
  useEffect(() => {
    const fetchUserCurrentLoansCount = async () => {
      if (isAuthenticated) {
        const accessToken = await getAccessTokenSilently();
        const url = `${import.meta.env.VITE_REACT_APP_API}/books/secure/currentloans/count`;
        const requestOptions = {
          method: "GET",
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const currentLoansCountResponse = await fetch(url, requestOptions);
        if (!currentLoansCountResponse.ok) {
          throw new Error("Something went wrong!");
        }
        console.log(currentLoansCountResponse);
        const currentLoansCountResponseJson =
          await currentLoansCountResponse.json();
        console.log(currentLoansCountResponseJson);
        setCurrentLoansCount(currentLoansCountResponseJson);
      }
      setIsLoadingCurrentLoansCount(false);
    };
    fetchUserCurrentLoansCount().catch((error: any) => {
      setIsLoadingCurrentLoansCount(false);
      setHttpError(error.message);
    });
  }, [isAuthenticated, isCheckedOut]);

  // Checking in database if current user checked out concrete book
  useEffect(() => {
    const fetchUserCheckOutBook = async () => {
      if (isAuthenticated) {
        const accessToken = await getAccessTokenSilently();
        const url = `${import.meta.env.VITE_REACT_APP_API}/books/secure/ischeckedout/byuser?bookId=${bookId}`;
        const requestOptions = {
          method: "GET",
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const bookCheckedOut = await fetch(url, requestOptions);

        if (!bookCheckedOut.ok) {
          throw new Error("Something went wrong!");
        }

        const bookCheckedOutResponseJson = await bookCheckedOut.json();

        setIsCheckedOut(bookCheckedOutResponseJson);
      }
      setIsLoadingBookCheckOut(false);
    };

    fetchUserCheckOutBook().catch((error: any) => {
      setIsLoadingBookCheckOut(false);
      setHttpError(error.message);
    });
  }, [isAuthenticated]);

  if (
    isLoading ||
    isLoadingReview ||
    isLoadingCurrentLoansCount ||
    isLoadingBookCheckOut ||
    isLoadingUserReview
  ) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  async function checkoutBook() {
    const accessToken = await getAccessTokenSilently();
    const url = `${import.meta.env.VITE_REACT_APP_API}/books/secure/checkout?bookId=${book?.id}`;
    const requestOptions = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${accessToken}`,
        "Content-Type": "application/json",
      },
    };
    const checkoutResponse = await fetch(url, requestOptions);
    if (!checkoutResponse.ok) {
      setDisplayError(true);
      window.scrollTo(0, 0);
      throw new Error("Something went wrong with checking out book!");
    }
    setDisplayError(false);
    setIsCheckedOut(true);
    window.scrollTo(0, 0);
  }

  async function submitReview(starInput: number, reviewDescription: string) {
    let bookId: number = 0;
    if (book?.id) {
      bookId = book.id;
    }

    const reviewRequestModel = new ReviewRequestModel(
      starInput,
      bookId,
      reviewDescription,
    );
    const url = `${import.meta.env.VITE_REACT_APP_API}/reviews/secure`;
    const token = await getAccessTokenSilently();
    const requestOptions = {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reviewRequestModel),
    };
    const returnResponse = await fetch(url, requestOptions);
    if (!returnResponse.ok) {
      throw new Error("Something went wrong with POSTing new review.");
    }
    setIsReviewLeft(true);
  }

  return (
    <div>
      <div className="container d-none d-lg-block">
        {displayError && (
          <div className="alert alert-danger mt-3" role="alert">
            Please pay outstanding fees and/or return late book(s).
          </div>
        )}
        <div className="row mt-5 ">
          <div className="col-sm-2 col-md-2">
            {book?.img ? (
              <img src={book?.img} width={226} height={349} alt="Book" />
            ) : (
              <img src={DefaultImg} width={226} height={349} alt="Book" />
            )}
          </div>
          <div className="col-4 col-md-4 container">
            <div className="ml-2">
              <h2>{book?.title}</h2>
              <h5 className="text-primary">{book?.author}</h5>
              <p className="lead">{book?.description}</p>
              <StarsReview rating={totalStars} size={32} />
            </div>
          </div>
          <CheckoutAndReviewBox
            book={book}
            mobile={false}
            currentLoansCount={currentLoansCount}
            isAuthenticated={isAuthenticated}
            isCheckedOut={isCheckedOut}
            checkoutBook={checkoutBook}
            isReviewLeft={isReviewLeft}
            submitReview={submitReview}
          />
        </div>
        <hr />
        <LatestReviews reviews={reviews} bookId={book?.id} mobile={false} />
      </div>
      <div className="container d-lg-none mt-5">
        {displayError && (
          <div className="alert alert-danger mt-3" role="alert">
            Please pay outstanding fees and/or return late book(s).
          </div>
        )}
        <div className="d-flex justify-content-center align-items-center">
          {book?.img ? (
            <img src={book?.img} width={226} height={349} alt="Book" />
          ) : (
            <img src={DefaultImg} width={226} height={349} alt="Book" />
          )}
        </div>
        <div className="mt-4">
          <div className="ml-2">
            <h2>{book?.title}</h2>
            <h5 className="text-primary">{book?.author}</h5>
            <p className="lead">{book?.description}</p>
            <StarsReview rating={totalStars} size={32} />
          </div>
        </div>
        <CheckoutAndReviewBox
          book={book}
          mobile={true}
          currentLoansCount={currentLoansCount}
          isAuthenticated={isAuthenticated}
          isCheckedOut={isCheckedOut}
          checkoutBook={checkoutBook}
          isReviewLeft={isReviewLeft}
          submitReview={submitReview}
        />
        <hr />
        <LatestReviews reviews={reviews} bookId={book?.id} mobile={true} />
      </div>
    </div>
  );
};

export default BookCheckoutPage;
