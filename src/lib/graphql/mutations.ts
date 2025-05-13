// lib/graphql/mutations.ts
import { gql } from '@apollo/client';

export const CREATE_NEWS_BLOG = gql`
  mutation CreateNewsBlog($input: NewsBlogInput!) {
    createNewsBlogs(news: $input) {
      id
      title
    }
  }
`;

export const CREATE_GAMING_BLOG = gql`
  mutation CreateGamingBlog($input: GamingBlogInput!) {
    createGamingBlogs(gaming: $input) {
      id
      title
    }
  }
`;
