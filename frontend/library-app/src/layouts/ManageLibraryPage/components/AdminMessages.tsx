import { useAuth0 } from "@auth0/auth0-react";
import { useEffect, useState } from "react";
import MessageModel from "../../../models/MessageModel";
import SpinnerLoading from "../../Utils/SpinnerLoading";
import Pagination from "../../Utils/Pagination";
import AdminMessage from "./AdminMessage";
import AdminMessageRequest from "../../../models/AdminMessageRequest";

const AdminMessages = () => {
  const { isAuthenticated, getAccessTokenSilently } = useAuth0();

  // Normal Loading Pieces
  const [isLoadingMessages, setIsLoadingMessages] = useState(true);
  const [httpError, setHttpError] = useState(null);

  // Messages endpoint State
  const [messages, setMessages] = useState<MessageModel[]>([]);
  const [messagesPerPage] = useState(5);

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  // Recall useEffect
  const [buttonSubmit, setButtonSubmit] = useState(false);

  useEffect(() => {
    const fetchUserMessages = async () => {
      if (isAuthenticated) {
        const url = `${import.meta.env.VITE_REACT_APP_API}/messages/search/findByClosed?closed=false&page=${currentPage - 1}&size=${messagesPerPage}`;
        const token = await getAccessTokenSilently();
        const requestOptions = {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        };
        const messageResponse = await fetch(url, requestOptions);
        if (!messageResponse.ok) {
          throw new Error(
            "Something went wrong with fetching messages by closed.",
          );
        }
        const messageResponseJson = await messageResponse.json();

        setMessages(messageResponseJson.content);
        setTotalPages(messageResponseJson.page.totalPages);
      }
      setIsLoadingMessages(false);
    };

    fetchUserMessages().catch((error: any) => {
      setIsLoadingMessages(false);
      setHttpError(error.message);
    });
    window.scrollTo(0, 0);
  }, [isAuthenticated, currentPage, buttonSubmit]);

  if (isLoadingMessages) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  async function submitResponseToQuestion(id: number, response: string) {
    const url = `${import.meta.env.VITE_REACT_APP_API}/messages/secure/admin/message`;
    if (isAuthenticated) {
      const token = await getAccessTokenSilently();
      const messageAdminRequestModel: AdminMessageRequest =
        new AdminMessageRequest(id, response);
      const requestOptions = {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(messageAdminRequestModel),
      };

      const messageRequestModelResponse = await fetch(url, requestOptions);

      if (!messageRequestModelResponse.ok) {
        throw new Error(
          "Something went wrong with adding answer to a question",
        );
      }
      setButtonSubmit(!buttonSubmit);
    }
  }

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <div className="mt-3">
      {messages.length > 0 ? (
        <>
          <h5>Pending Q/A</h5>
          {messages.map((message) => (
            <AdminMessage
              message={message}
              key={message.id}
              submitResponseToQuestion={submitResponseToQuestion}
            />
          ))}
        </>
      ) : (
        <h5>No pending Q/A</h5>
      )}
      {totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          paginate={paginate}
        />
      )}
    </div>
  );
};

export default AdminMessages;
