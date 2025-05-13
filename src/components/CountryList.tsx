'use client'
import { useQuery } from '@apollo/client';
import { GET_COUNTRIES } from '../lib/graphql/queries';

const CountryList = () => {
  const { loading, error, data } = useQuery(GET_COUNTRIES);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  return (
    <ul>
      {data.countries.map((country: any) => (
        <li key={country.code}>
          {country.name} - {country.continent.name}
        </li>
      ))}
    </ul>
  );
};

export default CountryList;
