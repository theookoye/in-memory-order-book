# **In-memory Order Book API**

## **Overview**

This project implements an order and trade matching service using **Vert.x**, following a **hexagonal architecture**.
The service handles order creation, trade execution, and order book management.

## **Getting Started**

### **1. Running Locally**

#### **Step 1: Configure API Key**

Set the API key in the configuration file at:  
📂 `app/src/main/resources/application.json`

```json
{
  "server": {
    "port": 8080
  },
  "security": {
    "api": {
      "key": "your-api-key-here"
    }
  }
}
```

#### **Step 2: Build the Project**

Run the following command from the project root:

`mvn clean install`

#### **Step 3: Start the Application**

Run the following command from the project root:

`mvn -pl app vertx:run`

The first command builds the application.
The second command runs the application locally.

### **2. Running with Docker**

The project includes a Dockerfile for containerized deployment.

#### **Step 1: Build the Docker Image**

`docker build -t vertxapp .`

#### **Step 2: Run the Container**

`docker run -p 8080:8080 vertxapp`

The application will be available at http://localhost:8080.

#### **Postman Collection**

A Postman collection with sample requests is available in the
📂 `examples/` folder.

To use it:

Open Postman. \
Import examples/api.postman_collection.json. \
Set the API key in the request headers.

### **Assumptions & Limitations**

- In-Memory Storage – Orders and trades are stored in-memory (no database).
- Basic Order Matching – Implements price-time priority, no advanced order types (e.g., stop-loss).
- Validation – Assumes valid inputs (price, quantity, etc.), and that price, quantity are sent as Strings to preserve
  significant figures and precision.
- Simplified Order Book – No aggregation or performance optimizations.
- Single Instance – No horizontal scaling or fault tolerance.
