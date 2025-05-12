import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchUsers } from "../../../../store/actions";
import { AiOutlineSearch } from "react-icons/ai";
import UserTable from "./UserTable";

const UserAdmin = () => {
  const dispatch = useDispatch();
  const { userLoading, userError } = useSelector((state) => state.errors);
  const { users } = useSelector((state) => state.users);

  console.log(users);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const handler = setTimeout(() => {
      dispatch(fetchUsers(search));
    }, 500);

    return () => clearTimeout(handler);
  }, [search]);

  if (userLoading) return <p>Loading users...</p>;
  if (userError) return <p className="text-red-500">Error: {userError}</p>;

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">User Management</h1>

      <div className="mb-6 flex flex-col sm:flex-row justify-between items-center gap-4">
        <div className="relative flex items-center 2xl:w-[450px] sm:w-[420px] w-full">
          <input
            type="text"
            placeholder="Search by username or email..."
            className="border border-gray-400 text-slate-800 rounded-md py-2 pl-4 pr-10 w-full focus:outline-none focus:ring-2 focus:ring-[#1976d2]"
            onChange={(e) => setSearch(e.target.value)}
          />
          <AiOutlineSearch className="absolute right-3 text-gray-500" />
        </div>
      </div>

      <UserTable users={users} />
    </div>
  );
};

export default UserAdmin;
