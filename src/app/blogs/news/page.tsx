import MarkdownViewer from "@/components/blogs-ui/blog-view";
import CreateBlog from "@/components/blogs-ui/create-blog";
import { NewsFeed } from "@/components/blogs-ui/news-feed";

export default function BlogNewsLayout() {

    return(
        <>
            <p className="text-2xl font-bold px-8 ">Gaming News</p>
            <NewsFeed />
            <CreateBlog />
            <MarkdownViewer />
        </>
    )
};