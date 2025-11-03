# API Documentation

## Authentication

### `POST /api/auth/register`

Register a new user.

**Sample Request Body:**

```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "firstname": "Test",
  "lastname": "User",
  "middlename": "Q"
}
```

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "message": "User registered successfully."
  }
}
```

**Sample Response Body (Error):**

```json
{
  "status": "failed",
  "message": "User with username 'testuser' already exists.",
  "data": null
}
```

### `POST /api/auth/login`

Login an existing user.

**Sample Request Body:**

```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "token": "ey...",
    "username": "testuser"
  }
}
```

**Sample Response Body (Error):**

```json
{
  "status": "failed",
  "message": "Invalid credentials.",
  "data": null
}
```

## User

### `GET /api/user/me`

Get the current user's profile information.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "profile_picture": "http://example.com/profile.jpg",
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test Q User",
    "createdAt": "2025-11-01T12:00:00Z",
    "lastUpdatedAt": "2025-11-01T12:00:00Z"
  }
}
```

### `PATCH /api/user/me`

Update the current user's profile information.

**Sample Request Body:**

```json
{
  "username": "newusername",
  "email": "new@example.com",
  "firstName": "New",
  "middleName": "W",
  "lastName": "Name"
}
```

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "profile_picture": "http://example.com/profile.jpg",
    "username": "newusername",
    "email": "new@example.com",
    "fullName": "New W Name",
    "createdAt": "2025-11-01T12:00:00Z",
    "lastUpdatedAt": "2025-11-01T12:05:00Z"
  }
}
```

### `DELETE /api/user/me`

Delete the current user's account.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": null
}
```

### `POST /api/user/me/profile-picture`

Upload a profile picture for the current user.

**Sample Request:**

This is a multipart/form-data request with a single file field named "file".

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": "http://res.cloudinary.com/..."
}
```

## Posts

### `POST /api/posts`

Create a new post.

**Sample Request Body:**

```json
{
  "contentLink": "http://example.com/post-image.jpg"
}
```

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "id": "...",
    "contentLink": "http://example.com/post-image.jpg",
    "ownerUsername": "testuser",
    "ownerFullName": "Test Q User",
    "ownerProfilePicture": "http://example.com/profile.jpg",
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2025-11-01T12:00:00Z",
    "editedAt": null
  }
}
```

### `GET /api/posts/{postId}`

Get a post by its ID.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "id": "...",
    "contentLink": "http://example.com/post-image.jpg",
    "ownerUsername": "testuser",
    "ownerFullName": "Test Q User",
    "ownerProfilePicture": "http://example.com/profile.jpg",
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2025-11-01T12:00:00Z",
    "editedAt": null
  }
}
```

### `PUT /api/posts/{postId}`

Update a post.

**Sample Request Body:**

```json
{
  "contentLink": "http://example.com/new-post-image.jpg"
}
```

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "id": "...",
    "contentLink": "http://example.com/new-post-image.jpg",
    "ownerUsername": "testuser",
    "ownerFullName": "Test Q User",
    "ownerProfilePicture": "http://example.com/profile.jpg",
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2025-11-01T12:00:00Z",
    "editedAt": "2025-11-01T12:05:00Z"
  }
}
```

### `DELETE /api/posts/{postId}`

Delete a post.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": null
}
```

## Comments

### `POST /api/posts/{postId}/comments`

Create a new comment on a post.

**Sample Request Body:**

```json
{
  "comment": "This is a great post!"
}
```

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "id": "...",
    "comment": "This is a great post!",
    "userUsername": "testuser",
    "userFullName": "Test Q User",
    "userProfilePicture": "http://example.com/profile.jpg",
    "createdAt": "2025-11-01T12:10:00Z",
    "editedAt": null
  }
}
```

### `GET /api/posts/{postId}/comments`

Get all comments for a post.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": [
    {
      "id": "...",
      "comment": "This is a great post!",
      "userUsername": "testuser",
      "userFullName": "Test Q User",
      "userProfilePicture": "http://example.com/profile.jpg",
      "createdAt": "2025-11-01T12:10:00Z",
      "editedAt": null
    }
  ]
}
```

### `PUT /api/posts/{postId}/comments/{commentId}`

Update a comment.

**Sample Request Body:**

```json
{
  "comment": "This is an updated comment."
}
```

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "id": "...",
    "comment": "This is an updated comment.",
    "userUsername": "testuser",
    "userFullName": "Test Q User",
    "userProfilePicture": "http://example.com/profile.jpg",
    "createdAt": "2025-11-01T12:10:00Z",
    "editedAt": "2025-11-01T12:15:00Z"
  }
}
```

### `DELETE /api/posts/{postId}/comments/{commentId}`

Delete a comment.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": null
}
```

## Likes

### `POST /api/posts/{postId}/like`

Like a post.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "liked": true,
    "likeCount": 1
  }
}
```

### `DELETE /api/posts/{postId}/like`

Unlike a post.

**Sample Response Body (Success):**

```json
{
  "status": "success",
  "data": {
    "liked": false,
    "likeCount": 0
  }
}
```
