package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Quick vote animation — two variants:
 * - Ballot drop (normal votes): ballot paper slides into box slot
 * - Checkbox check (checklist votes): checkbox appears and gets ticked
 * Subtle, fast, thematic. No flashbang, no particles.
 */
public class VoteQuickAnimation extends Div {

    private final boolean isChecklist;

    public VoteQuickAnimation(int remainingVotes, Runnable onComplete) {
        this.isChecklist = remainingVotes < 0;

        String voteText;
        if (isChecklist) {
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

        if (isChecklist) {
            return buildCheckboxJS(escaped);
        } else {
            return buildBallotJS(escaped);
        }
    }

    private String buildBallotJS(String escaped) {
        return
        // Overlay
        "var ov=document.createElement('div');ov.id='qk-ov';" +
        "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;display:flex;align-items:center;justify-content:center;flex-direction:column;background:rgba(30,30,47,.85);opacity:0;transition:opacity .3s ease;';" +
        "document.body.appendChild(ov);" +
        "requestAnimationFrame(function(){ov.style.opacity='1';});" +
        "\n" +
        // Ballot box — bigger
        "var box=document.createElement('div');" +
        "box.style.cssText='position:relative;width:140px;height:100px;margin-bottom:28px;';" +
        // Box body
        "var body=document.createElement('div');" +
        "body.style.cssText='position:absolute;bottom:0;left:15px;width:110px;height:65px;background:linear-gradient(135deg,#6C5CE7,#5A4BD1);border-radius:4px 4px 12px 12px;box-shadow:0 10px 30px rgba(108,92,231,.35);';" +
        // Box slot — wider
        "var slot=document.createElement('div');" +
        "slot.style.cssText='position:absolute;top:-1px;left:25px;width:90px;height:8px;background:#1E1E2F;border-radius:4px;box-shadow:inset 0 2px 6px rgba(0,0,0,.5);z-index:2;';" +
        // Ballot paper — bigger with more detail
        "var ballot=document.createElement('div');" +
        "ballot.style.cssText='position:absolute;top:-60px;left:35px;width:70px;height:50px;background:#F8F9FC;border:2px solid #A29BFE;border-radius:4px;z-index:3;opacity:0;transform:rotate(-5deg);transition:all .5s cubic-bezier(.34,1.56,.64,1);box-shadow:0 4px 12px rgba(0,0,0,.15);';" +
        // Checkmark circle on ballot — bigger
        "var check=document.createElement('div');" +
        "check.style.cssText='position:absolute;top:12px;left:18px;width:22px;height:22px;border:2.5px solid #6C5CE7;border-radius:50%;';" +
        // Checkmark tick — bigger
        "var tick=document.createElement('div');" +
        "tick.style.cssText='position:absolute;top:5px;left:5px;width:8px;height:12px;border-right:2.5px solid #6C5CE7;border-bottom:2.5px solid #6C5CE7;transform:rotate(45deg);';" +
        // Text lines on ballot — more detail
        "var l1=document.createElement('div');l1.style.cssText='position:absolute;top:12px;right:10px;width:20px;height:3px;background:#A29BFE;border-radius:2px;';" +
        "var l2=document.createElement('div');l2.style.cssText='position:absolute;top:20px;right:10px;width:14px;height:3px;background:#A29BFE;border-radius:2px;';" +
        "check.appendChild(tick);ballot.appendChild(check);ballot.appendChild(l1);ballot.appendChild(l2);" +
        "body.appendChild(slot);box.appendChild(body);box.appendChild(ballot);ov.appendChild(box);" +
        "\n" +
        // Text
        "var txt=document.createElement('div');" +
        "txt.textContent='" + escaped + "';" +
        "txt.style.cssText='font-family:Courier New,monospace;font-size:clamp(.9rem,3vw,1.3rem);font-weight:700;letter-spacing:.1em;color:#A29BFE;text-shadow:0 0 15px rgba(108,92,231,.4);opacity:0;transition:opacity .3s ease .6s;white-space:nowrap;';" +
        "ov.appendChild(txt);" +
        "\n" +
        "setTimeout(function(){ballot.style.opacity='1';ballot.style.top='10px';ballot.style.transform='rotate(0deg)';},200);" +
        "setTimeout(function(){txt.style.opacity='1';},600);";
    }

    private String buildCheckboxJS(String escaped) {
        return
        // Overlay
        "var ov=document.createElement('div');ov.id='qk-ov';" +
        "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;display:flex;align-items:center;justify-content:center;flex-direction:column;background:rgba(30,30,47,.85);opacity:0;transition:opacity .3s ease;';" +
        "document.body.appendChild(ov);" +
        "requestAnimationFrame(function(){ov.style.opacity='1';});" +
        "\n" +
        // Checkbox container
        "var cb=document.createElement('div');" +
        "cb.style.cssText='position:relative;width:60px;height:60px;margin-bottom:24px;';" +
        // Checkbox box
        "var box=document.createElement('div');" +
        "box.style.cssText='width:60px;height:60px;border:3px solid #A29BFE;border-radius:12px;background:transparent;transition:all .3s ease;box-sizing:border-box;';" +
        // Checkmark SVG — draws in
        "var svg=document.createElementNS('http://www.w3.org/2000/svg','svg');" +
        "svg.setAttribute('viewBox','0 0 24 24');svg.setAttribute('width','36');svg.setAttribute('height','36');" +
        "svg.style.cssText='position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);opacity:0;transition:opacity .2s ease .25s;';" +
        "var path=document.createElementNS('http://www.w3.org/2000/svg','path');" +
        "path.setAttribute('d','M5 13l4 4L19 7');" +
        "path.setAttribute('stroke','#6C5CE7');path.setAttribute('stroke-width','3');path.setAttribute('fill','none');" +
        "path.setAttribute('stroke-linecap','round');path.setAttribute('stroke-linejoin','round');" +
        "path.style.cssText='stroke-dasharray:30;stroke-dashoffset:30;transition:stroke-dashoffset .35s ease .3s;';" +
        "svg.appendChild(path);cb.appendChild(box);cb.appendChild(svg);ov.appendChild(cb);" +
        "\n" +
        // Text
        "var txt=document.createElement('div');" +
        "txt.textContent='" + escaped + "';" +
        "txt.style.cssText='font-family:Courier New,monospace;font-size:clamp(.9rem,3vw,1.3rem);font-weight:700;letter-spacing:.1em;color:#A29BFE;text-shadow:0 0 15px rgba(108,92,231,.4);opacity:0;transition:opacity .3s ease .6s;white-space:nowrap;';" +
        "ov.appendChild(txt);" +
        "\n" +
        // Animate
        "setTimeout(function(){box.style.background='rgba(108,92,231,.15)';box.style.borderColor='#6C5CE7';},200);" +
        "setTimeout(function(){svg.style.opacity='1';path.style.strokeDashoffset='0';},300);" +
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
