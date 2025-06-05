'use client'
import { useQuery } from '@apollo/client';
import { GET_COUNTRIES } from '../lib/graphql/queries';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import Channel from './channel-ui/Channel';
const CountryList = () => {
  const { loading, error, data } = useQuery(GET_COUNTRIES);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  return (
    // <ul>
    //   {data.countries.map((country: any) => (
    //     <li key={country.code}>
    //       {country.name} - {country.continent.name}
    //     </li>
    //   ))}
    // </ul>
    <Tabs defaultValue="account" className="w-[400px]">
  <TabsList>
    <TabsTrigger value="account">Account</TabsTrigger>
    <TabsTrigger value="password">Password</TabsTrigger>
  </TabsList>
  <TabsContent value="account"><Channel /></TabsContent>
  <TabsContent value="password">Change your password here.</TabsContent>
</Tabs>
  );
};

export default CountryList;
