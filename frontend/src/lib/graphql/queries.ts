import { gql } from '@apollo/client';

export const GET_COUNTRIES = gql`
  query {
    countries {
      code
      name
      continent {
        name
      }
    }
  }
`;

export const GET_NEWS_BLOGS = gql`
  query GetNewsBlogs {
    getNewsBlogs {
      newsBlog {
        id
        title
        hook
        description
        category
        channelId
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

export const GET_GAMING_NEWS = gql`
  query GetGamingNews {
    getNewsBlogsByCategory(category: "Gaming News") {
      newsBlog {
        id
        title
        hook
        description
        category
        channelId
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

export const GET_GAMING_BLOGS = gql`
  query GetGamingBlogs {
    getGamingBlogs {
      gamingBlog {
        id
        title
        hook
        description
        category
        channelId
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

export const GET_REVIEWS = gql`
  query GetReviews {
    getGamingBlogsByCategory(category: "Game Review") {
      gamingBlog {
        id
        title
        hook
        description
        category
        channelId
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

export const GET_NEWS_BLOG_BY_ID = gql`
  query GetNewsBlogById($id: ID!) {
    getNewsById(id: $id) {
      newsBlog {
        id
        title
        hook
        description
        category
        channelId
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