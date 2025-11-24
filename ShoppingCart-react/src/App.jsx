import { BrowserRouter, Routes, Route } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

import CreateAcc from "./CreateAcc.jsx";
import LoginPage from "./LoginPage.jsx";

function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/create-account" element={<CreateAcc />} />
      <Route path="/login" element={<LoginPage />} />
    </Routes>
  );
}

export default App;
