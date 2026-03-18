import App from "./App";
import Auth0ProviderWithHistory from "./auth/Auth0ProviderWithHistory";
import ProtectedRoute from "./auth/ProtectedRoute";
import BookCheckoutPage from "./layouts/BookCheckoutPage/BookCheckoutPage";
import ReviewListPage from "./layouts/BookCheckoutPage/ReviewListPage/ReviewListPage";
import HomePage from "./layouts/Homepage/HomePage";
import SearchBooksPage from "./layouts/SearchBooksPage/SearchBooksPage";
import { Navigate } from "react-router-dom";
import ShelfPage from "./layouts/ShelfPage/ShelfPage";
import MessagesPage from "./layouts/MessagesPage/MessagesPage";
import ManageLibraryPage from "./layouts/ManageLibraryPage/ManageLibraryPage";
import PaymentPage from "./layouts/PaymentPage/PaymentPage";

export const routes = [
  {
    path: "/",
    element: (
      <Auth0ProviderWithHistory>
        <App />
      </Auth0ProviderWithHistory>
    ),
    children: [
      { path: "search", Component: SearchBooksPage },
      { path: "/", Component: HomePage },
      { path: "checkout/:bookId", Component: BookCheckoutPage },
      { path: "reviewList/:bookId", Component: ReviewListPage },
      {
        Component: ProtectedRoute,
        children: [
          { path: "shelf", Component: ShelfPage },
          { path: "messages", Component: MessagesPage },
          { path: "admin", Component: ManageLibraryPage },
          { path: "fees", Component: PaymentPage },
        ],
      },
    ],
  },
  {
    path: "/homepage",
    element: <Navigate to="/" replace />,
  },
];
