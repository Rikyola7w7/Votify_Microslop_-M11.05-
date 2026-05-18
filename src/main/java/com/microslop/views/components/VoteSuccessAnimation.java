package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class VoteSuccessAnimation extends Div {

    private static final String[] CONFETTI_COLORS = {
        "#f59e0b", "#d97706", "#059669",
        "#10b981", "#3b82f6", "#6366f1", "#8b5cf6",
        "#ec4899", "#f43f5e"
    };

    public VoteSuccessAnimation(Runnable onComplete) {
        getStyle()
            .set("position", "fixed")
            .set("left", "0")
            .set("top", "0")
            .set("width", "100vw")
            .set("height", "100vh")
            .set("background", "rgba(15, 23, 42, 0.95)")
            .set("z-index", "10000")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("flex-direction", "column")
            .set("animation", "voteOverlayIn 0.4s ease-out forwards");

        Div container = new Div();
        container.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("gap", "2rem");

        Div ballotBox = buildBallotBox();
        Span successText = buildSuccessText();
        Span subText = buildSubText();
        Div confettiContainer = buildConfetti();

        container.add(ballotBox, successText, subText);
        add(container, confettiContainer);

        injectStyles();
        scheduleDismiss(onComplete);
    }

    private Div buildBallotBox() {
        Div ballotBox = new Div();
        ballotBox.getStyle()
            .set("position", "relative")
            .set("width", "120px")
            .set("height", "160px");

        Div boxBody = new Div();
        boxBody.getStyle()
            .set("position", "absolute")
            .set("bottom", "0")
            .set("left", "50%")
            .set("transform", "translateX(-50%)")
            .set("width", "100px")
            .set("height", "80px")
            .set("background", "linear-gradient(135deg, var(--accent) 0%, var(--accent-dark) 50%, #a16207 100%)")
            .set("border-radius", "8px 8px 12px 12px")
            .set("box-shadow", "0 20px 60px rgba(245, 158, 11, 0.4), inset 0 -10px 30px rgba(0,0,0,0.2), inset 0 10px 30px rgba(255,255,255,0.1)")
            .set("border", "4px solid #fbbf24")
            .set("border-top", "none");

        Div boxSlot = new Div();
        boxSlot.getStyle()
            .set("position", "absolute")
            .set("top", "15px")
            .set("left", "50%")
            .set("transform", "translateX(-50%)")
            .set("width", "60px")
            .set("height", "8px")
            .set("background", "var(--dark)")
            .set("border-radius", "4px")
            .set("box-shadow", "inset 0 2px 4px rgba(0,0,0,0.5)");

        Div ballot = new Div();
        ballot.getStyle()
            .set("position", "absolute")
            .set("width", "50px")
            .set("height", "65px")
            .set("background", "linear-gradient(180deg, #fef3c7 0%, #fde68a 50%, #fcd34d 100%)")
            .set("border-radius", "4px")
            .set("left", "50%")
            .set("top", "-80px")
            .set("transform", "translateX(-50%) rotate(0deg)")
            .set("box-shadow", "0 4px 15px rgba(253, 224, 71, 0.5)")
            .set("animation", "ballotDrop 1.2s cubic-bezier(0.34, 1.56, 0.64, 1) 0.3s forwards")
            .set("border", "2px solid #f59e0b");

        Div ballotCheck = new Div();
        ballotCheck.getStyle()
            .set("position", "absolute")
            .set("top", "50%")
            .set("left", "50%")
            .set("transform", "translate(-50%, -70%) rotate(45deg)")
            .set("width", "24px")
            .set("height", "24px")
            .set("border", "3px solid var(--success)")
            .set("border-radius", "50%")
            .set("border-right-color", "transparent")
            .set("border-bottom-color", "transparent");

        ballot.add(ballotCheck);
        boxBody.add(boxSlot);
        ballotBox.add(boxBody, ballot);
        return ballotBox;
    }

    private Span buildSuccessText() {
        Span successText = new Span("VOTE RECORDED");
        successText.getStyle()
            .set("color", "var(--accent)")
            .set("font-size", "2.5rem")
            .set("font-weight", "800")
            .set("letter-spacing", "0.15em")
            .set("text-shadow", "0 0 40px rgba(245, 158, 11, 0.6)")
            .set("animation", "textGlow 1.5s ease-in-out infinite alternate");
        return successText;
    }

    private Span buildSubText() {
        Span subText = new Span("Your voice has been counted");
        subText.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "1.1rem")
            .set("letter-spacing", "0.05em");
        return subText;
    }

    private Div buildConfetti() {
        Div confettiContainer = new Div();
        confettiContainer.getStyle()
            .set("position", "absolute")
            .set("top", "0")
            .set("left", "0")
            .set("width", "100%")
            .set("height", "100%")
            .set("pointer-events", "none")
            .set("overflow", "hidden");

        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < 50; i++) {
            Div confetti = new Div();
            int size = 8 + rand.nextInt(7);
            int left = rand.nextInt(101);
            double duration = 1.5 + rand.nextDouble() * 1.0;
            double delay = i * 0.05;
            boolean isCircle = rand.nextBoolean();

            confetti.getStyle()
                .set("position", "absolute")
                .set("width", size + "px")
                .set("height", size + "px")
                .set("background", CONFETTI_COLORS[i % CONFETTI_COLORS.length])
                .set("border-radius", isCircle ? "50%" : "2px")
                .set("left", left + "%")
                .set("top", "-20px")
                .set("animation", "confettiFall " + duration + "s linear " + delay + "s infinite");
            confettiContainer.add(confetti);
        }
        return confettiContainer;
    }

    private void injectStyles() {
        getElement().executeJs(
            "const style = document.createElement('style');" +
            "style.textContent = `" +
            "@keyframes ballotDrop {" +
            "  0% { top: -80px; transform: translateX(-50%) rotate(0deg); }" +
            "  60% { top: 50px; transform: translateX(-50%) rotate(-5deg); }" +
            "  80% { top: 45px; transform: translateX(-50%) rotate(3deg); }" +
            "  100% { top: 48px; transform: translateX(-50%) rotate(0deg); }" +
            "}" +
            "@keyframes confettiFall {" +
            "  0% { transform: translateY(0) rotate(0deg); opacity: 1; }" +
            "  100% { transform: translateY(100vh) rotate(720deg); opacity: 0; }" +
            "}" +
            "@keyframes voteOverlayIn {" +
            "  0% { opacity: 0; }" +
            "  100% { opacity: 1; }" +
            "}" +
            "@keyframes textGlow {" +
            "  0% { text-shadow: 0 0 20px rgba(251, 191, 36, 0.4); }" +
            "  100% { text-shadow: 0 0 60px rgba(251, 191, 36, 0.9), 0 0 100px rgba(251, 191, 36, 0.4); }" +
            "}" +
            "`;" +
            "document.head.appendChild(style);"
        );
    }

    private void scheduleDismiss(Runnable onComplete) {
        if (onComplete == null) {
            return;
        }
        getElement().executeJs(
            "setTimeout(function() {" +
            "  var el = document.querySelector('[style*=\"position: fixed\"]');" +
            "  if (el && el.style.zIndex === '10000') el.remove();" +
            "}, 2800);"
        );
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.accessLater(v -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {}
                onComplete.run();
            }, () -> {});
        }
    }
}