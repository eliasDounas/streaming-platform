import { BlogCard } from "@/components/blogs-ui/blog-card";
import MarkdownViewer from "@/components/blogs-ui/blog-view";
import { useQuery } from '@apollo/client';
import { useParams } from 'next/navigation';
import { GET_NEWS_BLOG_BY_ID } from '@/lib/graphql/queries';
export default function Page() {
    const { id } = useParams();
    
    const { loading, error, data } = useQuery(GET_NEWS_BLOG_BY_ID, {
        variables: { id },
        skip: !id, // Skip query execution if id is not available yet
    });

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error loading blog</div>;

    const { blog } = data;
    return (
        <>
        <BlogCard 
          key={blog.id}
          id={blog.id}
          title={blog.title}
          hook={blog.hook}
          writer={blog.writer}
          createdAt={blog.createdAt}
          readingTime={blog.readingTime}
          coverUrl={blog.coverImg}
          category='news'/>

        <MarkdownViewer />
        </>
    );
};