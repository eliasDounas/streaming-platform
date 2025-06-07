import { Input } from "@/components/ui/input"
import { Search } from "lucide-react"

const SearchBar = () => {
    return ( <>
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Search..."
              className="pl-10"
            />
    </> );
}
 
export default SearchBar;