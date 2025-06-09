// lib/graphql/mutations.ts
import { gql } from '@apollo/client';

export const CREATE_NEWS_BLOG = gql`
  mutation CreateNewsBlog($news: NewsBlogInput!) {
    createNewsBlogs(news: $news) {
      newsBlog {
        id
        title
        description
        category
        channelId
        hook
        content
        tags
        coverImg
        readingTime
        createdAt
      }
      channel {
        channelId
        name
        playbackUrl
        avatarUrl
      }
    }
  }
`;

export const CREATE_GAMING_BLOG = gql`
  mutation CreateGamingBlog($gaming: GamingBlogInput!) {
    createGamingBlogs(gaming: $gaming) {
      gamingBlog {
        id
        title
        description
        category
        channelId
        hook
        content
        tags
        coverImg
        readingTime
        createdAt
      }
      channel {
        channelId
        name
        playbackUrl
        avatarUrl
      }
    }
  }
`;
