import { useState } from "react";
import { useForm } from "react-hook-form";
import { FaUserPlus } from "react-icons/fa";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../shared/Inputfiels";
import { useDispatch } from "react-redux";
import { SignUpUser } from "../../store/actions";
import toast from "react-hot-toast";

const SignUp = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [loader, setLoader] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    mode: "onTouched",
  });

  const registerHandler = async (data) => {
    console.log("Register Click");
    dispatch(SignUpUser(data, toast, reset, navigate, setLoader));
  };
  return (
    <div className="min-h-[calc(100vh-64px)] flex justify-center items-center">
      <form
        onSubmit={handleSubmit(registerHandler)}
        className="sm:w-[450px] w-[360px] shadow-custom py-8 sm:px-8 px-4 rounded-md"
      >
        <div className="flex flex-col items-center justify-center space-y-4">
          <FaUserPlus className="text-slate-800 text-5xl" />
          <h1 className="text-slate-800 text-center font-montserrat lg:text-3xl text-2xl font-bold">
            Register Here
          </h1>
        </div>
        <hr className="mt-2 mb-5 text-black" />
        <div className="flex flex-col gap-3">
          <InputField
            label="UserName"
            required
            id="userName"
            type="text"
            message="*UserName is required"
            placeholder="Enter your username"
            register={register}
            errors={errors}
          />

          <InputField
            label="Email"
            required
            id="email"
            type="email"
            message="*Email is required"
            placeholder="Enter your email"
            register={register}
            errors={errors}
          />

          <InputField
            label="Password"
            required
            id="password"
            min={6}
            type="password"
            message="*Password is required"
            placeholder="Enter your password"
            register={register}
            errors={errors}
          />

          <InputField
            label="First Name"
            required
            id="firstName"
            min={2}
            type="firstName"
            message="*firstName is required"
            placeholder="Enter your first name"
            register={register}
            errors={errors}
          />

          <InputField
            label="Last Name"
            required
            id="lastName"
            min={3}
            type="lastName"
            message="*Last Name is required"
            placeholder="Enter your last name"
            register={register}
            errors={errors}
          />

          <InputField
            label="Phone Number"
            required
            id="phoneNumber"
            type="phonenumber"
            message="*Phone Number is required"
            placeholder="Enter your Phone Number"
            register={register}
            errors={errors}
          />
        </div>

        <button
          disabled={loader}
          className="bg-button-gradient flex gap-2 items-center justify-center font-semibold text-white w-full py-2 hover:text-slate-400 transition-colors duration-100 rounded-sm my-3"
          type="submit"
        >
          Register
        </button>

        <p className="text-center text-sm text-slate-700 mt-6">
          Already have an account?
          <Link
            className="font-semibold underline hover:text-black"
            to="/login"
          >
            <span> Login</span>
          </Link>
        </p>
      </form>
    </div>
  );
};

export default SignUp;
