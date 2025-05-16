-- member 테이블
CREATE TABLE `jis`.`member` (
  `memberId` VARCHAR(8) NOT NULL,
  `memberPwd` VARCHAR(200) NOT NULL,
  `memberName` VARCHAR(12) NULL,
  `mobile` VARCHAR(13) NULL,
  `email` VARCHAR(50) NULL,
  `registerDate` DATETIME NULL DEFAULT now(),
  `memberImg` VARCHAR(45) NOT NULL DEFAULT 'avatar.png',
  `memberPoint` INT NULL DEFAULT 100,
  `gender` VARCHAR(1) NOT NULL DEFAULT 'U',
  PRIMARY KEY (`memberId`),
  UNIQUE INDEX `mobile_UNIQUE` (`mobile` ASC) VISIBLE,
  UNIQUE INDEX `emial_UNIQUE` (`email` ASC) VISIBLE);

-- 회원 저장
use jis;
insert into member(memberId, memberPwd, memberName, mobile, email) 
		values('asdf123', sha1(md5('1234')), '홍길동', '010-1111-2222', 'abc@abc.com');

-- 계층형 게시판 테이블
CREATE TABLE `jis`.`hboard` (
  `boardNo` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(20) NOT NULL,
  `content` VARCHAR(2000) NULL,
  `writer` VARCHAR(8) NULL,
  `postDate` DATETIME NULL DEFAULT now(),
  `readCount` INT NULL DEFAULT 0,
  `ref` INT NULL DEFAULT 0,
  `step` INT NULL DEFAULT 0,
  `refOrder` INT NULL DEFAULT 0,
  PRIMARY KEY (`boardNo`),
  INDEX `fk_hboard_member_idx` (`writer` ASC) VISIBLE,
  CONSTRAINT `fk_hboard_member`
    FOREIGN KEY (`writer`)
    REFERENCES `jis`.`member` (`memberId`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
COMMENT = '계층형 게시판';

insert into hboard(title, content, writer) values('게시판 생성', '많은 이용 바랍니다', 'asdf123');

select * from hboard;



-- boardupfiles 테이블
CREATE TABLE `jis`.`boardupfiles` (
  `fileNo` INT NOT NULL AUTO_INCREMENT,
  `originalFileName` VARCHAR(100) NOT NULL,
  `newFileName` VARCHAR(150) NOT NULL,
  `thumbFileName` VARCHAR(150) NULL,
  `isImage` TINYINT(4) NULL DEFAULT 0,
  `ext` VARCHAR(20) NULL,
  `size` BIGINT(20) NULL,
  `boardNo` INT NULL,
  `base64` LONGTEXT NULL,
  `filePath` VARCHAR(200) NULL,
  PRIMARY KEY (`fileNo`),
  INDEX `fk_upfiles_hboard_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `fk_upfiles_hboard`
    FOREIGN KEY (`boardNo`)
    REFERENCES `jis`.`hboard` (`boardNo`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
COMMENT = '게시물에 업로드되는 파일정보';

-- 업로드 파일 저장
insert into boardupfiles (
	boardNo, originalFileName, newFileName, thumbFileName, isImage, ext, size, base64, filePath
) values (
	#{boardNo}, #{originalFileName}, #{newFileName}, #{thumbFileName}, #{isImage}, #{ext}, #{size}, #{base64}, #{filePath}
);

select * from boardupfiles where boardNo = 7;

-- 8번 글 : hboard, boardupfiles, member
-- 조인
select h.boardNo, h.title, h.content, h.writer, h.postDate, h.readCount, h.ref, h.step, h.refOrder,
f.*, m.memberName, m.email
from hboard h left outer join boardupfiles f
on h.boardNo = f.boardNo
inner join member m
on h.writer = m.memberId
where h.boardNo = 8;

-- 조회수 처리
-- boardreadlog 테이블 생성
CREATE TABLE `jis`.`boardreadlog` (
  `boardReadLogNo` INT NOT NULL AUTO_INCREMENT,
  `readWho` VARCHAR(130) NOT NULL,
  `readWhen` DATETIME NULL DEFAULT now(),
  `boardNo` INT NULL,
  PRIMARY KEY (`boardReadLogNo`),
  INDEX `fk_boardreadlog_boardno_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `fk_boardreadlog_boardno`
    FOREIGN KEY (`boardNo`)
    REFERENCES `jis`.`hboard` (`boardNo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '게시글 조회 정보를 저장하는 테이블';

-- ipAddr유저가 boardNo번 글을 조회한 적이 있는지?
select * from boardreadlog where boardNo = 8 and readWho = '127.0.0.1';

insert into boardreadlog (readWho, boardNo) values ('127.0.0.1', 8);

select DATEDIFF(now(), readWhen) as datediff from boardreadlog where readWho = '127.0.0.1' and boardNo = 8;

select ifnull((select Hour(timediff(now(), readwhen)) from boardreadlog where readWho = '127.0.0.1' and boardNo = 8), -1);

-- hboard에서 조회수(readCount) 1 증가
update hboard set readcount = readcount + 1 where boardNo = 8;

-- 24시간이 지났다면, readWhen을 업데이트
update boardreadlog set readWhen = now() where readWho = '127.0.0.1' and boardNo = 8;

-- 답글 처리

-- 부모글에 대한 다른 답글이 있는 상태에서, 부모글의 답글이 추가되는 경우
-- (자리확보를 위해) 기존의 답글의 refOrder값을 update해야 한다.
update hboard set refOrder = refOrder + 1 where ref = #{ref} and refOrder > #{refOrder};

-- 답글을 입력받아서 답글을 저장하고, 부모글의 boardNo를 ref에, 
-- 부모글의 step + 1을 step에, 부모글의 refOrder + 1을 refOrder에 저장한다.
insert into hboard (title, content, writer, ref, step, refOrder)
values(#{title}, #{content}, #{writer}, #{ref}, #{step}, #{refOrder});

-- 게시글 수정
update hboard set title = #{title}, content = #{content} where boardNo = #{boardNo};

select boardNo, title, content, writer, ref, postDate, readCount, ref, step, refOrder
from hboard where boardNo = #{boardNo};

select * from boardupfiles where boardNo = #{boardNo};

-- 페이징 (pagination)
-- 데이터 수가 많아지면, 목록페이지를 가져올 때, 많은 시간이 걸릴 수 있고, 출력하는 데도 많은 자원이 소모된다.
-- 일반적으로 페이징을 통해 최소한의 데이터를 보여주는 방식 선호.
-- 페이징 처리 하면 데이터베이스에서 필요한 만큼의 최소한의 데이터를 가져오고 출력하므로 성능 개선에 도움이 된다.
-- limit 이용
-- select ... limit 보여주기시작할rowIndex번호, 1페이지당보여줄글의갯수

-- pageNo = 1
-- pagingSize = 10
use jis;
use jis;
select * from hboard order by ref desc, refOrder asc limit 0, 10;

-- pageNo = 2
select * from hboard order by ref desc, refOrder asc limit 10, 10;
-- pageNo = 3
select * from hboard order by ref desc, refOrder asc limit 20, 10;

-- pageNo = 102
select * from hboard order by ref desc, refOrder asc limit 1010, 10;

-- pageNo = 103
-- select * from hboard order by ref desc, refOrder asc limit (pageNo - 1) * 10, 10;
select * from hboard order by ref desc, refOrder asc limit 1020, 10;

select count(*) from hboard;
-- 총 글의 갯수 1026
-- 1026 / 10 = 102.6
-- 마지막 페이지번호 : 103
-- 마지막 페이지번호 -> 총글의 갯수 / 10 -> 올림

-- paging block
-- 1 블럭 : start = 1, end = 10
-- 2 블럭 : start = 11, end = 20
-- 3 블럭 : start = 21, end = 30
-- ...
-- 11 블럭 : start = 101, end = 110

-- 게시글 삭제
-- isDelete 컬럼 추가
ALTER TABLE `jis`.`hboard` 
ADD COLUMN `isDelete` CHAR(1) NULL DEFAULT 'N' AFTER `refOrder`;

-- 첨부파일 삭제
delete from boardupfiles where boardNo = #{boardNo};

-- 게시글 삭제 (isDelete = 'Y'로 업데이트)
boardupfilesupdate hboard set isDelete = 'Y', title = '', content = '' where boardNo = #{boardNo};
;
use jis;
-- 게시글 검색
-- type == c (내용), keyword='테스트'
select * from hboard where title like concat('%', '테스트', '%');

-- type == tc (제목이나 내용) , keyword='테스트'
select * from hboard where content like concat('%', '테스트', '%') or title like concat('%', '테스트', '%');

-- 트랜잭션

CREATE TABLE `jis`.`table_a` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `jis`.`table_b` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
  
  -- 포인트 정책
  CREATE TABLE `jis`.`pointdef` (
  `pointWhy` ENUM('SIGNUP', 'LOGIN', 'WRITE', 'REPLY') NOT NULL,
  `pointScore` INT NOT NULL,
  PRIMARY KEY (`pointWhy`))
COMMENT = '멤버에게 적립할 포인트에 대한 정책 정의한 테이블';


CREATE TABLE `jis`.`pointlog` (
  `pointLogNo` INT NOT NULL AUTO_INCREMENT,
  `pointWho` VARCHAR(8) NOT NULL,
  `pointWhen` DATETIME NULL DEFAULT now(),
  `pointWhy` ENUM('SIGNUP', 'LOGIN', 'WRITE', 'REPLY') NOT NULL,
  `pointScore` INT NULL,
  PRIMARY KEY (`pointLogNo`),
  INDEX `fk_pointlog_member_idx` (`pointWho` ASC) VISIBLE,
  INDEX `fk_pointlog_pointdef_idx` (`pointWhy` ASC) VISIBLE,
  CONSTRAINT `fk_pointlog_member`
    FOREIGN KEY (`pointWho`)
    REFERENCES `jis`.`member` (`memberId`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_pointlog_pointdef`
    FOREIGN KEY (`pointWhy`)
    REFERENCES `jis`.`pointdef` (`pointWhy`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
COMMENT = '멤버에게 지급된 포인트 기록 테이블';

-- score 얻어오기
select pointScore from pointdef where pointWhy = #{pointWhy};

insert into pointlog(pointWho, pointWhy, pointScore) values(#{pointWho}, #{pointWhy}, #{pointScore});

update member set memberPoint = memberPoint + #{pointScore}  where memberId = #{memberId};
;
-- 회원가입
insert into member (memberId, memberPwd, memberName, mobile, email, registerDate, memberImg, gender)
values (#{memberId}, #{memberPwd}, #{memberName}, #{mobile}, #{email}, #{registerDate}, #{memberImg}, #{gender});
;
-- hboard에 boardType 컬럼
ALTER TABLE `jis`.`hboard` 
ADD COLUMN `boardType` VARCHAR(10) NULL AFTER `isDelete`;

-- 자동로그인
-- 멤버테이블 수정
ALTER TABLE `jis`.`member` 
ADD COLUMN `sesId` VARCHAR(40) NULL AFTER `gender`,
ADD COLUMN `allimit` DATETIME NULL AFTER `sesId`;

-- 자동로그인 정보를 db에 저장
update member set sesId = #{sesId}, allimit = #{allimit}
where memberId = #{memberId}
;

-- 쿠키에 자동로그인한다고 저장되어 있을 때, 자동로그인하는 쿼리문
select * from member where sesId = #{sesId} and allimit > now()
;

-- 942109F1A55675DE5C19BC2B7CF7DAB0
select * from member where sesId = '942109F1A55675DE5C19BC2B7CF7DAB0' and allimit > now();

-- 자동로그인 데이터 삭제
update member set sesId = null, allimit = null where memberId = #{memberId}
;

-- 댓글 테이블
CREATE TABLE `jis`.`comment` (
  `commentNo` INT NOT NULL AUTO_INCREMENT,
  `commenter` VARCHAR(8) NULL,
  `content` VARCHAR(500) NULL,
  `regDate` DATETIME NULL DEFAULT now(),
  `boardNo` INT NULL,
  PRIMARY KEY (`commentNo`),
  INDEX `kf_comment_member_idx` (`commenter` ASC) VISIBLE,
  INDEX `fk_comment_hboard_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `fk_comment_member`
    FOREIGN KEY (`commenter`)
    REFERENCES `jis`.`member` (`memberId`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_hboard`
    FOREIGN KEY (`boardNo`)
    REFERENCES `jis`.`hboard` (`boardNo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '댓글 테이블';

-- 댓글 저장
insert into comment(commenter, content, boardNo) values('user01', 'ddds?', '1036');

select * from comment where boardNo = #{boardNo} order by commentNo desc
;

-- 댓글 목록 조회 + 페이징
select * from comment where boardNo = 1036 order by commentNo desc
limit #{skip}, #{pagingSize};