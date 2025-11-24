import React, {useRef, useEffect, useState} from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function LoginPage() {
  const usernameElement = useRef();
  const passwordElement = useRef();
  const navigate = useNavigate();
  const [errors, setErrors] = useState({});

    function validateInputs() {
        const errors = {};
        const username = usernameElement.current.value.trim();
        const password = passwordElement.current.value.trim();

        if (!username) errors.username = "Username is required";
        if (!password) errors.password = "Password is required";
        return errors;
    }

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/login", { withCredentials: true })
      .then((response) => {
        const role = response.data.role;
        if (role === "customer") {
          window.location.href = "http://localhost:8080/products";
        } else if (role === "admin") {
          window.location.href = "http://localhost:8080/admin/home";
        }
      })
      .catch((e) => {
        console.log(e);
      });
  }, []);

  function handleLogin(evt) {
    evt.preventDefault();

      const validationErrors = validateInputs();
      if (Object.keys(validationErrors).length > 0) {
          setErrors(validationErrors);
          return;
      }

      setErrors({});

    loginUser();
  }

  function loginUser() {


    const data = {
      username: usernameElement.current.value,
      password: passwordElement.current.value,
    };
    axios
      .post("http://localhost:8080/api/login", data, {
        withCredentials: true,
        headers: { "Content-Type": "application/json" },
      })
      .then((response) => {
        const role = response.data.role;
        if (role === "admin") {
          window.location.href = "http://localhost:8080/admin";
        } else if (role === "customer") {
          window.location.href = "http://localhost:8080/products";
        }
      })
      .catch((error) => {
        console.log("Login failed", error);
        alert("Invalid username or password");
        passwordElement.current.value = "";
        usernameElement.current.value = "";
      });
  }

  return (
    <div
      style={{
        backgroundColor: "#f8f9fa",
        height: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        fontFamily: "'Segoe UI', sans-serif",
      }}
    >
      <div
        className="login-card bg-white p-5 rounded-4 shadow p-3"
        style={{ width: "100%", maxWidth: "450px" }}
      >
        <h2 className="text-center mb-4 fw-semibold text-dark">
          Shopping Cart Login
        </h2>

        <form onSubmit={handleLogin}>
          <div className="mb-3">
            <label
              htmlFor="username"
              className="form-label fw-medium text-secondary"
            >
              Username:
            </label>
            <input
              type="text"
              id="username"
              ref={usernameElement}
              placeholder="Username"
              className="form-control"
            />
              {errors.username && (
                  <div className="text-danger small mt-1">{errors.username}</div>
              )}
          </div>

          <div className="mb-3">
            <label
              htmlFor="password"
              className="form-label fw-medium text-secondary"
            >
              Password:
            </label>
            <input
              type="password"
              id="password"
              ref={passwordElement}
              placeholder="Password"
              className="form-control"
            />
              {errors.password && (
                  <div className="text-danger small mt-1">{errors.password}</div>
              )}
          </div>

          <div className="d-grid">
            <button type="submit" className="btn btn-primary py-2">
              Login
            </button>
          </div>
        </form>

        <div className="text-center mt-3">
          <button
            className="btn btn-link btn-sm p-0 text-secondary"
            onClick={() => navigate("/create-account")}
          >
            No Account? Create here
          </button>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
