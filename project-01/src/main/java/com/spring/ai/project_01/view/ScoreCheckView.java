package com.spring.ai.project_01.view;

import com.spring.ai.project_01.entity.AssignmentScore;
import com.spring.ai.project_01.repository.ScoreRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.Route;
import java.util.List;

/**
 * Vaadin 프레임워크를 사용해 구현한 학생용 성적 대시보드입니다.
 * 별도의 HTML/JS 없이 오직 자바 코드로만 화면을 구성합니다.
 */
@Route("") // http://localhost:8081 접속 시 이 화면이 메인으로 노출됩니다.
public class ScoreCheckView extends VerticalLayout {

    private final ScoreRepository repository;
    private final Grid<AssignmentScore> grid = new Grid<>(AssignmentScore.class, false);

    public ScoreCheckView(ScoreRepository repository) {
        this.repository = repository;

        // 1. 전체 레이아웃 디자인 (중앙 정렬 및 여백 설정)
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // 2. 대시보드 제목 설정
        H1 title = new H1("📊 내 과제 점수 히스토리");
        title.getStyle().set("color", "#2c3e50"); // 가독성을 위한 진한 남색 스타일 적용

        // 3. 검색 입력창 (GitHub ID 입력)
        TextField githubIdField = new TextField();
        githubIdField.setPlaceholder("GitHub ID를 입력하세요");
        githubIdField.setPrefixComponent(VaadinIcon.USER.create()); // 사용자 아이콘 추가
        githubIdField.setClearButtonVisible(true);
        githubIdField.setWidth("300px");
        githubIdField.focus(); // 페이지 접속 시 바로 입력 가능하도록 포커스 설정

        // 4. 조회 버튼 및 이벤트 연결
        Button searchBtn = new Button("조회", VaadinIcon.SEARCH.create());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // 강조를 위한 파란색 테마
        searchBtn.addClickShortcut(Key.ENTER); // 엔터키 입력 시 즉시 조회 실행
        searchBtn.addClickListener(e -> searchHistory(githubIdField.getValue()));

        HorizontalLayout searchLayout = new HorizontalLayout(githubIdField, searchBtn);
        searchLayout.setAlignItems(Alignment.BASELINE);

        // 5. 결과 테이블(Grid) 설정 호출
        configureGrid();

        // 6. 모든 컴포넌트를 화면에 조립
        add(title, searchLayout, grid);
    }

    /**
     * 성적 데이터를 보여줄 표(Grid)의 상세 설정을 담당합니다.
     */
    private void configureGrid() {
        grid.setWidth("90%");
        grid.setHeight("600px");
        grid.setVisible(false); // 데이터 조회 전까지는 숨김 처리

        // 그리드 내 자동 줄바꿈 테마 적용 (피드백 내용이 길 경우 대비)
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        // [컬럼 1] 과제 리포지토리명
        grid.addColumn(AssignmentScore::getRepoName)
                .setHeader("과제명")
                .setWidth("150px")
                .setFlexGrow(0);

        // [컬럼 2] Pull Request 번호
        grid.addColumn(AssignmentScore::getPrNumber)
                .setHeader("PR #")
                .setWidth("80px")
                .setFlexGrow(0);

        // [컬럼 3] 점수 (구간별 색상 배지 적용)
        grid.addColumn(new ComponentRenderer<>(score -> {
            Span badge = new Span(score.getScore() + "점");
            String theme = "badge pill";
            if (score.getScore() >= 90) theme += " success"; // 90점 이상: 초록색
            else if (score.getScore() >= 70) theme += " contrast"; // 70점 이상: 회색
            else theme += " error"; // 그 외: 빨간색
            badge.getElement().getThemeList().add(theme);
            return badge;
        })).setHeader("점수").setWidth("100px").setSortable(true).setFlexGrow(0);

        // [컬럼 4] AI 조교의 상세 피드백 (가독성 최적화 스타일 적용)
        grid.addColumn(new ComponentRenderer<>(score -> {
                    Span span = new Span(score.getFeedback());
                    // 줄바꿈 인식 및 자동 개행 스타일 설정
                    span.getStyle().set("white-space", "pre-wrap");
                    span.getStyle().set("word-break", "break-word");
                    span.getStyle().set("line-height", "1.5");
                    span.setWidthFull();
                    return span;
                }))
                .setHeader("AI 피드백")
                .setWidth("350px")
                .setFlexGrow(1); // 남은 공간을 피드백 칸이 가득 채우도록 설정

        // [컬럼 5] 채점 일시 (포맷팅 적용)
        grid.addColumn(new LocalDateTimeRenderer<>(
                AssignmentScore::getGradedAt,
                "yyyy-MM-dd HH:mm"
        )).setHeader("채점 일시").setWidth("160px").setFlexGrow(0);
    }

    /**
     * 입력받은 ID로 성적 이력을 조회하여 표에 뿌려줍니다.
     */
    private void searchHistory(String studentName) {
        if (studentName == null || studentName.isBlank()) {
            Notification.show("GitHub ID를 입력해주세요.", 2000, Notification.Position.MIDDLE);
            return;
        }

        // 최신순으로 정렬된 리스트 조회
        List<AssignmentScore> history = repository.findByStudentNameOrderByGradedAtDesc(studentName);

        if (history.isEmpty()) {
            grid.setVisible(false);
            Notification.show("'" + studentName + "' 님의 채점 기록이 없습니다.", 3000, Notification.Position.MIDDLE);
        } else {
            grid.setVisible(true);
            grid.setItems(history);
            Notification.show(history.size() + "건의 내역을 성공적으로 불러왔습니다.", 2000, Notification.Position.BOTTOM_END);
        }
    }
}