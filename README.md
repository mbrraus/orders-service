# ORDER SERVICE - API DOCUMENTATION

## Overview

Order Service is responsible for:

* Managing customers
* Creating and querying orders
* Applying validations (customer status, quantity rules)
* Returning the correct HTTP status codes

## Base Url

http://localhost:8080

## Customer API

### POST /customers

Create a new customer.

**Request body**

```json
{
  "fullName": "Muberra Seyma Uslu",
  "email": "mbrraus@example.com"
}
```
**Success Response - 201 Created**

```json
{
  "id": 1,
  "email": "mbrraus@example.com",
  "fullName": "Muberra Seyma Uslu",
  "createdDate": "2025-11-24T12:23:47.3718401"
}
```
**Error Responses**

**Case:** Duplicate email  
**Status:** 409  
**Response:** Duplicate email for the customer

### GET /customers/{id}

Returns a customer by ID.

**Example Response - 200 OK**

```json
{
  "id": 1,
  "email": "mbrraus@example.com",
  "fullName": "Muberra Seyma Uslu",
  "createdDate": "2025-11-24T12:23:47.3718401"
}
```

**Error Responses**

**Case:** Not Found  
**Status:** 404  
**Response:** Customer not found

## Order API

### POST /orders

Create a new order.

**Request body**

```json
{
  "customerId": 1,
  "orderItems": [
      {"sku": "p-1001", "quantity": 1, "unitPrice":20} 
    ]
}
```

### Validations

- **Customer must exist and be ACTIVE**  
  *If not → 400 BAD REQUEST*  
  **Response:** `"Customer not found or blocked"`

- **Order must contain at least one item**  
  *If not → 400 BAD REQUEST*  
  **Response:** `"Order must contain at least one item"`

- **Item quantity must be ≥ 1**  
  *If not → 400 BAD REQUEST*  
  **Response:** `Order item quantity must be at least 1`


**Note:** Normally, unit price should come from Catalog Service. For now, request body accepts unitPrice.

**Success Response - 201 Created**

```json
{
    "orderId": 1,
    "customerId": 1,
    "totalAmount": 20,
    "orderItems": [
        {
          "sku": "p-1001",
          "productName": "todo",
          "unitPrice": 20,
          "quantity": 1
        }
    ],
    "orderStatus": "NEW",
    "createdAt": "2025-11-24T14:31:05.6040613"
}
```

### GET /orders/{id}

Returns a single order by ID.

**Success Response - 200 OK**

```json
{
    "orderId": 1,
    "customerId": 1,
    "totalAmount": 20.00,
    "orderItems": [
    {
        "sku": "p-1001",
        "productName": "todo",
        "unitPrice": 20.00,
        "quantity": 1
      }
    ],
    "orderStatus": "NEW",
    "createdAt": "2025-11-24T14:31:05.6040613"
  }
```

**Error Responses**

**Case**: Not found  
**Status**: 404  
**Response**: Order not found

### GET /orders (Filtering, Pagination, Sorting)

### Query Parameters

- **customerId** (`Long`)  
  *Required: No*  
  Filters orders belonging to the given Customer ID.

- **status** (`ENUM: NEW, PAID, CANCELLED`)  
  *Required: No*  
  Filters orders by their status.

- **page** (`int`)  
  *Required: No  
  Default: 0*  
  Pagination page index.

- **size** (`int`)  
  *Required: No  
  Default: 10*  
  Number of records per page.

- **sort** (`String`)  
  *Required: No  
  Default: createdAt,desc*  
  Sorting field and direction.

**Example Request**

`GET /orders?customerId=1&status=NEW&page=0&size=10&sort=createdAt,desc`

**Example Response - 200 OK**

```json
{
    "content": [ ],
    "pageable": { },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "number": 0,
    "size": 10
}
```
