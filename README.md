# 자바 스프링 강의 4주차
### 목차
+ [유닛 테스트](#유닛-테스트)
  - [도메인 유닛테스트](#도메인-유닛테스트)
  - [서비스 계층 유닛테스트](#서비스-계층-유닛테스트)
  - [Repository 유닛테스트](#repository-유닛테스트)
  - [Controller 유닛테스트](#controller-유닛테스트)
---
#### 지난주 코드 활용  
```
git clone https://github.com/kidongYun/spring_lecture_week3
```  

## 유닛 테스트
* 함수, 기능 수준에서의 테스트 코드 작성
* 특정 기능에 대한 문서 수준의 기능을 제공
* 운영되고 있는 서비스를 리팩토링하는 것은 불안정하므로, 충분한 유닛 테스트코드가 작성되어 있으면 코드를 리팩토링할 수 있어서 안정적으로 변경이 가능

## TDD 테스트 주도 개발
* 테스트 코드를 먼저 작성하고 함수의 스펙을 정한다.
* 테스트 코드가 성공 될 수 있도록 소스 코드를 작성한다.

## 도메인 유닛테스트
### Article.update() 테스트 코드 작성
* 테스트 코드를 먼저 작성하여 실패하는 테스트를 만든다.
* 만드려고 하는 함수의 의도, 틀을 먼저 잡는 과정
```java
class ArticleTest {
    @Test
    public void update_호출하면_title_content_필드_값이_변경되어야_한다() {
        // given
        Article article = Article.builder()
                .title("title before")
                .content("content before")
                .build();

        // when
        article.update("title after", "content after");

        // then
        assertThat(article.getTitle()).isEqualTo("title after");
        assertThat(article.getContent()).isEqualTo("content after");
    }
}
```
* 테스트대상이 되는 객체(클래스명)에서 alt+enter -> 테스트코드작성(Create Test) 팁이 뜬다 -> ok
* `@test` : 테스코드로 동작
* 테스트코드에 들어가는 함수명은 테스트목적을 명확하고 최대한 자세하게 명시 - 함수 동작을 알려주는 문서 역할을 하기도 한다
* 테스트 코드 패턴 : *given, when, then*
```java
public class Article {
    // ...
  
    public void update(String title, String content) {
//        this.title = title;
//        this.content = content;
    }
    
    // ...
}
```
* update() 함수를 테스트 코드가 합격할 수 있도록 구현
```java
public class Article {
    // ...
  
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    // ...
}
```
### Article.of() 테스트 코드 작성
```java
class ArticleTest {
    // ...
  
    @Test
    public void of_호출하면_Article_객체를_반환해야_한다() {
        // given
        ArticleDto.ReqPost request = ArticleDto.ReqPost.builder()
                .title("title")
                .content("content")
                .build();

        // when
        Article article = Article.of(request);

        // then
        assertThat(article.getTitle()).isEqualTo("title");
        assertThat(article.getContent()).isEqualTo("content");
    }
}
```
```java
public class Article {
  // ...
  public static Article of(ArticleDto.ReqPost from) {
    return Article.builder()
//                .title(from.getTitle())
//                .content(from.getContent())
            .build();
  }
  
  // ...
}
```
* of() 함수가 테스트 코드를 통과할 수 있도록 변경
```java
public class Article {
  // ...
  public static Article of(ArticleDto.ReqPost from) {
    return Article.builder()
                .title(from.getTitle())
                .content(from.getContent())
            .build();
  }
  
  // ...
}
```
### 작성한 테스트 코드 한번에 돌리기
* gradle > verification > test 실행
* CI/CD 환경을 구축하게 된다면 형상이 PUSH 되었을 때 지속적으로 테스트 코드를 확인하게 하여 이상이 없는지 오류를 검출한다

## 서비스 계층 유닛테스트
### Mocking의 필요성
* 기능에 외부와의 의존성이 있다면 배제하는 것이 어려우므로 테스트를 위해 Mocking 처리를 한다.  
*Mocking : 가짜 객체주입*
* 테스트의 속도를 위해, 서비스 레이어에 필요한 빈들만 가져오기 위해서 Mockito 라이브러리를 활용해 모두 Mock 객체를 가져온다.
```java
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
  @InjectMocks
  private ArticleService articleService;

  @Mock
  private ArticleRepository articleRepository;

  @Test
  public void findById_id_에_맞는_Article_객체를_가져온다() {
    // given
    Long idStub = 1L;
    Optional<Article> articleOptStub = Optional.of(Article.builder()
            .id(idStub).title("title").content("content").build());
    when(articleRepository.findById(idStub)).thenReturn(articleOptStub);

    // when
    Article result = articleService.findById(idStub);

    // then
    assertThat(result.getId()).isEqualTo(articleOptStub.get().getId());
  }
}
```
* `@ExtendWith(MockitoExtension.class)` : Mockito라이브러리를 사용하겠다 선언
* `@InjectMocks` : 가짜 객체 대상이라는 것을 선언
* ArticleService 객체가 가지고 있는 의존성에도 가짜 객체를 넣어준다
* `@Mock` : 가짜로 만들어진 개체
* Mocking 을 통해 ArticleService 객체가 가지고 있는 의존성을 제거한다
* 이를 활용하여 ArticleService 로직만을 집중하여 테스트가 가능
* 정말 의미 있는 테스트 코드를 작성했는지 한번씩 확인해주기

### 예외 테스트하기
```java
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    // ...
  @Test
  public void findById_id_에_맞는_Article_객체가_없다면_DATA_IS_NOT_FOUND_오류발생() {
    // given
    Long idStub = 1L;

    // when
    when(articleRepository.findById(idStub)).thenReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> articleService.findById(idStub))
            .isInstanceOf(ApiException.class)
            .hasMessage("article is not found");
  }
}
```
* 가짜 객체이기때문에 `when(articleRepository.findById(idStub)).thenReturn(articleOptStub);`가 없으면 Null 오류가 난다.
### AnyMatcher 활용
고정적인 stub 객체를 생성하는 것 대신 any 함수들을 사용할 수도 있다
```java
Long idStub = anyLong();
```
## Repository 유닛테스트
```java
@ExtendWith(SpringExtension.class)
@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;
}

```
* `@ExtendWith(SpringExtension.class)` : 실제 Spring Container 에서 빈을 가져온다  
(Service와 달리 의존성을 많이 가지고 있지 않기 때문에)
* `@DataJpaTest`
  * Spring Container 에서 Repository 에 해당하는 빈들만 가져온다.
  * 데이터베이스 벤더를 자동으로 H2Database 를 적용해준다  
  이는 테스트하고 바로 휘발되기때문에 인메모리 데이터베이스로 테스트 목적으로 사용하기 적합
  * 하나의 테스트 코드들이 하나의 트랜잭션으로 잡혀서 다른 테스트 코드와는 독립적으로 테스트 되도록 구성한다
* 등록된 스프링 컨테이너에서 *ArticleRepository 의존성*을 주입
### 테스트코드 작성을 위해 간단한 쿼리메소드 구현
```java
public interface ArticleRepository extends CrudRepository<Article, Long> {
    List<Article> findByTitle(String title);
}
```
* JPA 는 메소드 명을 정해진 규칙에 따라 작성하면 간단한 쿼리들을 구현할 수 있도록 한다
* JPA 는 쿼리를 작성하기 위해서 다양한 방법을 제공한다
  * 쿼리 메소드
  * @Query 를 활용한 native 쿼리
  * QueryDSL 라이브러리 활용 등등..
* 세부 내용이 궁금하다면 JPA 내용을 확인
```java
@ExtendWith(SpringExtension.class)
@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void findByTitle_title_조회하면_일치하는_Article_객체들이_반환된다() {
        // given
        String javaStub = "Java";
        String pythonStub = "Python";

        articleRepository.save(Article.builder().title(javaStub).build());
        articleRepository.save(Article.builder().title(javaStub).build());
        articleRepository.save(Article.builder().title(pythonStub).build());

        // when
        List<Article> articles = articleRepository.findByTitle(javaStub);

        // then
        assertThat(articles.size()).isEqualTo(2);
        assertTrue(articles.stream().allMatch(a -> javaStub.equals(a.getTitle())), "title 필드 값이 모두 " + javaStub + " 인가?");
    }
}
```
## Controller 유닛테스트
### MockMvc를 활용해서 테스트 코드 구현
```java
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @MockBean
    ArticleService articleService;
    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Response.ok() 함수 호출시 code 값이 ApiCode.SUCCESS 값을 가져야 한다.")
    public void get_whenYouCallOk_ThenReturnSuccessCode() throws Exception {
        // given
        Long idStub = 1L;
        when(articleService.findById(idStub)).thenReturn(Article.builder().id(idStub).build());

        // when, then
        mvc.perform(get("/api/v1/article/" + idStub)
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}
```
* `@WebMvcTest` : Controller와 관련된 객체만 스프링 컨테이너빈으로 등록
* `@MockBean` : Mock 객체를 스프링 컨테이너에 등록
* 가짜 api client(e.g. postman) 역할을 하는 **MockMvc** 추가  
(컨트롤러 함수들은 api진입점으로 활용되기 때문이다.)
* `@DisplayName` : 실제 함수명과 함수명 출력을 다르게 할 때

### Result까지 비교하는 테스트 코드 구현
```java
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
//...
    public void get_whenYouCallOk_ThenReturnSuccessCode() throws Exception {
        // given
        Long idStub = 1L;
        when(articleService.findById(idStub)).thenReturn(Article.builder().id(idStub).build());

        // when
        String response = mvc.perform(get("/api/v1/article/" + idStub)
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(ApiCode.SUCCESS.getName()).isSubstringOf(response);
    }
}

```
---
### 참고
https://github.com/kidongYun/spring_lecture_week_4