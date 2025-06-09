import { ReviewsFeed } from "@/components/blogs-ui/reviews-feed";

export default function BlogReviewsLayout() {

    return(
        <>
            <p className="text-2xl font-bold px-8 ">Game Reviews</p>
            <ReviewsFeed />
        </>
    )
};