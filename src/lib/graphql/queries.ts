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

export const GET_NEWS_BLOG_BY_ID = gql`
  query GetNewsBlogById($id: ID!) {
    getNewsById(id: $id) {
      id
      title
      hook
      content
      createdAt
      tags
      coverImg
      readingTime
      createdAt
    }
  }
`;