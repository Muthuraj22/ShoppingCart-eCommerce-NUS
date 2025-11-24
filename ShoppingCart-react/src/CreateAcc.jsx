import React, { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function CreateAcc() {
  const navigate = useNavigate();

  const nameElement = useRef();
  const usernameElement = useRef();
  const passwordElement = useRef();
  const confirmPasswordElement = useRef();
  const emailElement = useRef();
  const addressElement = useRef();
  const phoneElement = useRef();
  const [errors, setErrors] = useState({});
  const role = "customer";

  function validateInputs() {
    const errors = {};
    const name = nameElement.current.value.trim();
    const username = usernameElement.current.value.trim();
    const email = emailElement.current.value.trim();
    const password = passwordElement.current.value.trim();
    const confirmPassword = confirmPasswordElement.current.value.trim();
    const address = addressElement.current.value.trim();
    const phone = phoneElement.current.value.trim();

    if (!name) errors.name = "Name is required";
    if (!username) errors.username = "Username is required";

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) errors.email = "Email is required";
    else if (!emailRegex.test(email)) errors.email = "Invalid email address";

    if (!password) errors.password = "Password is required";
    else if (password.length < 6)
      errors.password = "Password must be at least 6 characters";

    if (password !== confirmPassword)
      errors.confirmPassword = "Passwords do not match";

    if (!address) errors.address = "Address is required";

    const phoneRegex = /^[0-9]{8}$/;
    if (!phone) errors.phone = "Phone number is required";
    else if (!phoneRegex.test(phone))
      errors.phone = "Invalid phone number (digits only)";

    return errors;
  }

  function handleCreateClick(e) {
    e.preventDefault();

    const validationErrors = validateInputs();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setErrors({});

    const data = {
      name: nameElement.current.value.trim(),
      username: usernameElement.current.value.trim(),
      password: passwordElement.current.value.trim(),
      emailAddress: emailElement.current.value.trim(),
      phoneNumber: phoneElement.current.value.trim(),
      address: addressElement.current.value.trim(),
      role,
    };

    axios
      .post("http://localhost:8080/api/customer/save", data, {
        withCredentials: true,
        headers: { "Content-Type": "application/json" },
      })
      .then((response) => {
        alert(response.data.message);
        navigate("/login");
      })
      .catch((error) => {
        if (error.response && error.response.status === 409) {
          setErrors({ username: "Username already exists" });
        } else {
          alert("An error occurred: " + error.message);
        }
      });
  }

  return (
    <div className="bg-light min-vh-100 d-flex justify-content-center align-items-center">
      <div
        className="bg-white p-5 rounded-4 shadow p-3"
        style={{ width: "100%", maxWidth: "600px" }}
      >
        <h2 className="text-center mb-4 fw-semibold text-dark">
          Create a New Account
        </h2>

        <form onSubmit={handleCreateClick} noValidate>
          {/* Name */}
          <div className="mb-3">
            <label className="form-label fw-medium text-secondary">Name:</label>
            <input type="text" ref={nameElement} className="form-control" />
            {errors.name && (
              <div className="text-danger small mt-1">{errors.name}</div>
            )}
          </div>

          {/* Username */}
          <div className="mb-3">
            <label className="form-label fw-medium text-secondary">
              Username:
            </label>
            <input type="text" ref={usernameElement} className="form-control" />
            {errors.username && (
              <div className="text-danger small mt-1">{errors.username}</div>
            )}
          </div>

          {/* Email */}
          <div className="mb-3">
            <label className="form-label fw-medium text-secondary">
              Email:
            </label>
            <input type="email" ref={emailElement} className="form-control" />
            {errors.email && (
              <div className="text-danger small mt-1">{errors.email}</div>
            )}
          </div>

          {/* Password */}
          <div className="mb-3">
            <label className="form-label fw-medium text-secondary">
              Password:
            </label>
            <input
              type="password"
              ref={passwordElement}
              className="form-control"
            />
            {errors.password && (
              <div className="text-danger small mt-1">{errors.password}</div>
            )}
          </div>

          {/* Confirm Password */}
          <div className="mb-3">
            <label className="form-label fw-medium text-secondary">
              Retype Password:
            </label>
            <input
              type="password"
              ref={confirmPasswordElement}
              className="form-control"
            />
            {errors.confirmPassword && (
              <div className="text-danger small mt-1">
                {errors.confirmPassword}
              </div>
            )}
          </div>

          {/* Address */}
          <div className="mb-3">
            <label className="form-label fw-medium text-secondary">
              Address:
            </label>
            <input type="text" ref={addressElement} className="form-control" />
            {errors.address && (
              <div className="text-danger small mt-1">{errors.address}</div>
            )}
          </div>

          {/* Phone */}
          <div className="mb-4">
            <label className="form-label fw-medium text-secondary">
              Phone:
            </label>
            <input type="tel" ref={phoneElement} className="form-control" />
            {errors.phone && (
              <div className="text-danger small mt-1">{errors.phone}</div>
            )}
          </div>

          <div className="d-grid mb-3">
            <button type="submit" className="btn btn-primary py-2">
              Create Account
            </button>
          </div>

          <div className="d-grid">
            <button
              type="button"
              className="btn btn-outline-secondary py-2"
              onClick={() => navigate("/")}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateAcc;
