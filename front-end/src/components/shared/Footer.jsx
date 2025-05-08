import React from "react";

const Footer = () => {
  return (
    <footer className="bg-gray-800 text-white py-4 mt-8">
      <div className="container mx-auto text-center">
        <p>
          &copy; {new Date().getFullYear()} E-Shop Rookies. All rights
          reserved.
        </p>
        <p>
          <a href="/about" className="text-blue-400 hover:underline">
            About Us
          </a>{" "}
          |{" "}
          <a href="/contact" className="text-blue-400 hover:underline">
            Contact
          </a>{" "}
          |{" "}
          <a href="/about" className="text-blue-400 hover:underline">
            Privacy Policy
          </a>
        </p>
      </div>
    </footer>
  );
};

export default Footer;
