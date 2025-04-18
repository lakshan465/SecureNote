import axios from "axios";

console.log("API URL:", process.env.REACT_APP_API_URL);

// Create an Axios instance
const api = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}/api`,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});

// Add a request interceptor to include JWT and CSRF tokens
// api.interceptors.request.use(
//   async (config) => {
//     const token = localStorage.getItem("JWT_TOKEN");
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`;
//     }

//     let csrfToken = localStorage.getItem("CSRF_TOKEN");
//     if (!csrfToken) {
//       try {
//         const response = await axios.get(
//           `${process.env.REACT_APP_API_URL}/api/csrf-token`,
//           { withCredentials: true }
//         );
//         csrfToken = response.data.token;
//         localStorage.setItem("CSRF_TOKEN", csrfToken);
//       } catch (error) {
//         console.error("Failed to fetch CSRF token", error);
//       }
//     }

//     if (csrfToken) {
//       config.headers["X-XSRF-TOKEN"] = csrfToken;
//     }
//     console.log("X-XSRF-TOKEN " + csrfToken);
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

let cachedCsrfToken = null;

api.interceptors.request.use(
  async (config) => {
    const token = localStorage.getItem("JWT_TOKEN");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    if (!cachedCsrfToken) {
      cachedCsrfToken = localStorage.getItem("CSRF_TOKEN");
    }

    if (!cachedCsrfToken) {
      try {
        const response = await axios.get(
          `${process.env.REACT_APP_API_URL}/api/csrf-token`,
          { withCredentials: true }
        );
        cachedCsrfToken = response.data.token;
        localStorage.setItem("CSRF_TOKEN", cachedCsrfToken);
      } catch (error) {
        console.error("Failed to fetch CSRF token", error);
      }
    }

    if (cachedCsrfToken) {
      config.headers["X-XSRF-TOKEN"] = cachedCsrfToken;
      console.log("Using CSRF token:", cachedCsrfToken);
    }

    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
