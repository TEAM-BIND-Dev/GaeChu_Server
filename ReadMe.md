# GaeChu-Server Like API 문서 (컨트롤러 가이드)

아래 문서는 LikeController를 빠르게 이해하고 사용할 수 있도록 API, 요청 경로, 요청 파라미터/바디, 응답 예시를 정리한 것입니다.

- Base URL: /api/likes
- 인증: 현재 컨트롤러 레벨에서는 별도 인증 로직 없음(프로젝트 전반 정책에 따름)
- 응답 포맷: 성공 시 DTO 혹은 본문 없음, 실패 시 CustomException을 GlobalExceptionHandler가 처리하여 상태코드와 메시지 문자열을 반환합니다.

참고: categoryId 해석 규칙

- 숫자 문자열: 해당 숫자를 카테고리 ID로 간주합니다. 예: "3" → 3
- 카테고리 이름 문자열: 내부 캐시(DataInitializer)에서 이름으로 ID를 조회합니다. 없으면 400 INVALID_CATEGORY
- null, "null", 공백: null로 간주
	- 일부 엔드포인트는 null을 0으로 치환하여 사용합니다(아래 각 API 설명에 명시)


1) 좋아요/좋아요 취소

- Method/Path: POST /api/likes/{categoryId}/{referenceId}
- Query Params:
	- likerId (string, required) 좋아요를 수행하는 사용자 ID
	- isLike (boolean, required) true면 좋아요, false면 좋아요 취소
- Path Params:
	- categoryId (string, required) 숫자ID | 카테고리이름 | null(문자열) | 공백 → 내부적으로 null이면 0으로 변환
	- referenceId (string, required) 대상(게시물 등) 식별자
- Request Body: 없음
- Response: 204 No Content
- Error:
	- 400 Bad Request: INVALID_CATEGORY (카테고리 이름을 ID로 변환하지 못한 경우)

예시(cURL)

- 좋아요
  curl -X POST "http://localhost:8080/api/likes/board/12345?likerId=user-1&isLike=true"

- 좋아요 취소
  curl -X POST "http://localhost:8080/api/likes/board/12345?likerId=user-1&isLike=false"

- categoryId를 숫자로 사용하는 경우
  curl -X POST "http://localhost:8080/api/likes/3/12345?likerId=user-1&isLike=true"

- categoryId를 null로 주는 경우(null 문자열)
  curl -X POST "http://localhost:8080/api/likes/null/12345?likerId=user-1&isLike=true"


2) 특정 reference + category 조합의 상세 조회

- Method/Path: GET /api/likes/detail/{categoryId}/{referenceId}
- Path Params:
	- categoryId (string, required) 숫자ID | 카테고리이름 | null(문자열) | 공백 → null로 전달됨(0으로 치환하지 않음)
	- referenceId (string, required)
- Query Params: 없음
- Request Body: 없음
- Response: 200 OK, LikeDetailResponse
  {
  "referenceId": "12345",
  "likeCount": 10,
  "likerIds": ["user-1", "user-2", "user-3"]
  }
- Error:
	- 400 Bad Request: INVALID_CATEGORY (카테고리 이름 변환 실패 시)
	- 404 Not Found: LIKE_NOT_FOUND (해당 조합을 찾지 못한 경우; 서비스 로직 상황에 따름)

예시(cURL)
curl "http://localhost:8080/api/likes/detail/board/12345"
curl "http://localhost:8080/api/likes/detail/3/12345"
curl "http://localhost:8080/api/likes/detail/null/12345"

3) 여러 referenceId에 대한 like count 일괄 조회

- Method/Path: GET /api/likes/count/{categoryId}
- Path Params:
	- categoryId (string, required) 숫자ID | 카테고리이름 | null(문자열) | 공백 → 내부적으로 null이면 0으로 변환
- Query Params:
	- referenceIds (List<string>, required) 같은 키를 반복하여 전달. 예: referenceIds=1&referenceIds=2
- Request Body: 없음
- Response: 200 OK, List<LikeCountResponse>
  [
  { "referenceId": "12345", "likeCount": 10 },
  { "referenceId": "67890", "likeCount": 2 }
  ]
- Error:
	- 400 Bad Request: INVALID_CATEGORY

예시(cURL)
curl "http://localhost:8080/api/likes/count/board?referenceIds=12345&referenceIds=67890"
curl "http://localhost:8080/api/likes/count/3?referenceIds=12345&referenceIds=67890"

4) 특정 사용자(작성자)가 좋아요한 레퍼런스 목록과 각 like count 조회

- Method/Path: GET /api/likes/count/{categoryId}/{userId}
- Path Params:
	- categoryId (string, required) 숫자ID | 카테고리이름 | null(문자열) | 공백 → null로 전달됨(0으로 치환하지 않음)
	- userId (string, required)
- Query Params: 없음
- Request Body: 없음
- Response: 200 OK, List<LikeCountResponse>
  [
  { "referenceId": "12345", "likeCount": 10 },
  { "referenceId": "77777", "likeCount": 5 }
  ]
- Error:
	- 400 Bad Request: INVALID_CATEGORY

예시(cURL)
curl "http://localhost:8080/api/likes/count/board/user-1"
curl "http://localhost:8080/api/likes/count/3/user-1"
curl "http://localhost:8080/api/likes/count/null/user-1"

에러 응답 형식(GlobalExceptionHandler)

- 이 서버는 CustomException을 잡아 상태코드와 메시지 문자열을 본문으로 그대로 반환합니다.
- 예)
	- 상태: 400 Bad Request
	- Body(plain text): 허용하지않은 카테고리입니다

정의된 ErrorCode

- INVALID_CATEGORY
	- code: CAT_001
	- message: 허용하지않은 카테고리입니다
	- httpStatus: 400
- LIKE_NOT_FOUND
	- code: LIKE_001
	- message: Like Not Found
	- httpStatus: 404

비고

- categoryId에 이름을 사용하는 경우, DataInitializer의 캐시에서 이름→ID 매핑을 찾습니다. 없으면 INVALID_CATEGORY 발생
- detail, count/{categoryId}/{userId}는 categoryId가 null이면 null로 서비스에 전달됩니다. 반면, POST like와 count/{categoryId}는 null이면
  0으로 변환되어 사용됩니다.

---

Postman 복붙용 빠른 테스트 모음

- 로컬 Base URL: http://localhost:8080
- 서버 Base URL(예시): https://api.your-domain.com
- 공통 헤더(필요 시):
	- Content-Type: application/json

1) 좋아요/좋아요 취소

- Method: POST
- URL 템플릿: {{BASE_URL}}/api/likes/{categoryId}/{referenceId}?likerId={{likerId}}&isLike={{isLike}}
- 예시(좋아요):
  http://localhost:8080/api/likes/board/12345?likerId=user-1&isLike=true
- 예시(좋아요 취소):
  http://localhost:8080/api/likes/board/12345?likerId=user-1&isLike=false
- 예시(카테고리 숫자):
  http://localhost:8080/api/likes/3/12345?likerId=user-1&isLike=true
- 예시(categoryId를 null 문자열로):
  http://localhost:8080/api/likes/null/12345?likerId=user-1&isLike=true
- Body: 없음 (raw/body 비워두세요)

Postman 설정 팁:

- Method: POST
- Params 탭에 key/value 추가
	- likerId = user-1
	- isLike = true
- Body 탭: None

2) 특정 reference + category 상세 조회

- Method: GET
- URL 템플릿: {{BASE_URL}}/api/likes/detail/{categoryId}/{referenceId}
- 예시:
  http://localhost:8080/api/likes/detail/board/12345
  http://localhost:8080/api/likes/detail/3/12345
  http://localhost:8080/api/likes/detail/null/12345
- Body: 없음
- 예시 응답:
  {
  "referenceId": "12345",
  "likeCount": 10,
  "likerIds": ["user-1", "user-2", "user-3"]
  }

3) 여러 referenceId의 like count 일괄 조회

- Method: GET
- URL 템플릿: {{BASE_URL}}/api/likes/count/{categoryId}?referenceIds={id1}&referenceIds={id2}...
- 예시:
  http://localhost:8080/api/likes/count/board?referenceIds=12345&referenceIds=67890
  http://localhost:8080/api/likes/count/3?referenceIds=12345&referenceIds=67890
- Body: 없음
- 예시 응답:
  [
  { "referenceId": "12345", "likeCount": 10 },
  { "referenceId": "67890", "likeCount": 2 }
  ]

Postman 설정 팁:

- Params 탭에서 referenceIds를 같은 키로 여러 번 추가하세요.

4) 특정 사용자(작성자)가 좋아요한 레퍼런스 목록과 각 like count 조회

- Method: GET
- URL 템플릿: {{BASE_URL}}/api/likes/count/{categoryId}/{userId}
- 예시:
  http://localhost:8080/api/likes/count/board/user-1
  http://localhost:8080/api/likes/count/3/user-1
  http://localhost:8080/api/likes/count/null/user-1
- Body: 없음
- 예시 응답:
  [
  { "referenceId": "12345", "likeCount": 10 },
  { "referenceId": "77777", "likeCount": 5 }
  ]

에러 응답(참고)

- 상태: 400 Bad Request, 본문: 허용하지않은 카테고리입니다
- 상태: 404 Not Found, 본문: Like Not Found

Postman 환경 변수 예시

- 환경에 BASE_URL 변수를 만들어 사용하면 URL 재사용이 쉬워집니다.
	- BASE_URL = http://localhost:8080
- 예: {{BASE_URL}}/api/likes/board/12345?likerId=user-1&isLike=true
