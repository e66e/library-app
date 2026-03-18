import type ReviewModel from "../../models/ReviewModel";
import StarsReview from "./StarsReview";

interface Props {
  review: ReviewModel;
}

const Review = (props: Props) => {
  //   console.log(props.review);

  const date = new Date(props.review.date);

  const longMonth = date.toLocaleString("en-GB", { month: "long" });
  const dateDay = date.getDate();
  const dateYear = date.getFullYear();

  const dateRender = dateDay + " " + longMonth + ", " + dateYear;

  //   console.log(dateRender);

  return (
    <div>
      <div className="col-sm-8 col-md-8">
        <h5>{props.review.userEmail}</h5>
        <div className="row">
          <div className="col">{dateRender}</div>
          <div className="col">
            <StarsReview rating={props.review.rating} size={16} />
          </div>
        </div>
        <div className="mt-2">
          <p>{props.review.reviewDescription}</p>
        </div>
      </div>
      <hr />
    </div>
  );
};

export default Review;
