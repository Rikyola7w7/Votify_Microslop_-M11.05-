package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Quick vote animation — a ballot paper drops into a box slot.
 * Subtle, fast, thematic. No flashbang, no particle storms.
 * Just a clean "ballot cast" metaphor that feels satisfying.
 */
public class VoteQuickAnimation extends Div {

    public VoteQuickAnimation(int remainingVotes, Runnable onComplete) {
        String voteText;
        if (remainingVotes < 0) {
            voteText = "CHECKLIST VOTE RECORDED";
        } else if (remainingVotes > 0) {
            voteText = remainingVotes + " VOTE" + (remainingVotes != 1 ? "S" : "") + " LEFT";
        } else {
            voteText = "NO VOTES LEFT";
        }

        getElement().executeJs(buildAnimationJS(voteText));
        scheduleDismiss(onComplete);
    }

    private String buildAnimationJS(String voteText) {
        String escaped = voteText.replace("'", "\\'");
        return
        // Overlay
        "var ov=document.createElement('div');ov.id='qk-ov';" +
        "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;display:flex;align-items:center;justify-content:center;flex-direction:column;background:rgba(30,30,47,.85);opacity:0;transition:opacity .3s ease;';" +
        "document.body.appendChild(ov);" +
        "requestAnimationFrame(function(){ov.style.opacity='1';});" +
        "\n" +
        // Ballot box
        "var box=document.createElement('div');" +
        "box.style.cssText='position:relative;width:100px;height:80px;margin-bottom:24px;';" +
        // Box body
        "var body=document.createElement('div');" +
        "body.style.cssText='position:absolute;bottom:0;left:10px;width:80px;height:50px;background:linear-gradient(135deg,#6C5CE7,#5A4BD1);border-radius:0 0 8px 8px;box-shadow:0 8px 24px rgba(108,92,231,.3);';" +
        // Box slot
        "var slot=document.createElement('div');" +
        "slot.style.cssText='position:absolute;top:0;left:20px;width:60px;height:6px;background:#1E1E2F;border-radius:3px;box-shadow:inset 0 2px 4px rgba(0,0,0,.4);z-index:2;';" +
        // Ballot paper — drops in
        "var ballot=document.createElement('div');" +
        "ballot.style.cssText='position:absolute;top:-40px;left:30px;width:40px;height:30px;background:#F8F9FC;border:2px solid #A29BFE;border-radius:3px;z-index:3;opacity:0;transform:rotate(-5deg);transition:all .5s cubic-bezier(.34,1.56,.64,1);';" +
        // Checkmark on ballot
        "var check=document.createElement('div');" +
        "check.style.cssText='position:absolute;top:8px;left:12px;width:14px;height:14px;border:2px solid #6C5CE7;border-radius:50%;';" +
        "var tick=document.createElement('div');" +
        "tick.style.cssText='position:absolute;top:4px;left:3px;width:6px;height:8px;border-right:2px solid #6C5CE7;border-bottom:2px solid #6C5CE7;transform:rotate(45deg);';" +
        "check.appendChild(tick);ballot.appendChild(check);" +
        "body.appendChild(slot);box.appendChild(body);box.appendChild(ballot);ov.appendChild(box);" +
        "\n" +
        // Text
        "var txt=document.createElement('div');" +
        "txt.textContent='" + escaped + "';" +
        "txt.style.cssText='font-family:Courier New,monospace;font-size:clamp(.9rem,3vw,1.3rem);font-weight:700;letter-spacing:.1em;color:#A29BFE;text-shadow:0 0 15px rgba(108,92,231,.4);opacity:0;transition:opacity .3s ease .6s;white-space:nowrap;';" +
        "ov.appendChild(txt);" +
        "\n" +
        // Animate ballot drop after a short delay
        "setTimeout(function(){" +
        "  ballot.style.opacity='1';" +
        "  ballot.style.top='8px';" +
        "  ballot.style.transform='rotate(0deg)';" +
        "},200);" +
        "\n" +
        // Show text after ballot drops
        "setTimeout(function(){txt.style.opacity='1';},600);";
    }

    private void scheduleDismiss(Runnable onComplete) {
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('qk-ov');" +
            "  if(ov){ov.style.opacity='0';setTimeout(function(){ov.remove();},400);}" +
            "},1800);"
        );

        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.accessLater(v -> {
                try { Thread.sleep(2200); } catch (InterruptedException ignored) {}
                onComplete.run();
            }, () -> {});
        }
    }
}
