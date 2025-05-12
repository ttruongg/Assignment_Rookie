import React, { useState } from "react";
import { MdCheckCircleOutline } from "react-icons/md";
import { TbXboxX } from "react-icons/tb";

const UserTable = ({ users }) => {
  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  };

  if (!users) {
    return (
      <div className="p-6 text-center text-gray-500">
        User list is loading or not available.
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6 mt-6">
      {users.map((user) => (
        <div
          key={user.id}
          className="bg-white shadow-md rounded-lg p-6 border cursor-pointer relative"
        >
          <h2 className="text-xl font-bold mb-4 text-gray-800">
            {user.userName}
          </h2>

          <div className="space-y-2 text-gray-700">
            <div>
              <span className="font-semibold">Full Name:</span> {user.firstName}{" "}
              {user.lastName}
            </div>
            <div>
              <span className="font-semibold">Email:</span> {user.email}
            </div>
            <div>
              <span className="font-semibold">Phone:</span> {user.phoneNumber}
            </div>
            <div>
              <span className="font-semibold">Roles:</span>{" "}
              {user.roles.join(", ")}
            </div>
            <div>
              <span className="font-semibold">Created On:</span>{" "}
              {formatDate(user.createdOn)}
            </div>
            <div>
              <span className="font-semibold">Last Updated:</span>{" "}
              {formatDate(user.lastUpdatedOn)}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default UserTable;
