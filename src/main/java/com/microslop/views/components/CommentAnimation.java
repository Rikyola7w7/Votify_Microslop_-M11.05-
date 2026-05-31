package com.microslop.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

/**
 * Comment posted animation — a speech bubble pops in with a subtle writing line.
 * Light, fast, thematic for feedback/comments.
 * Under 1.5 seconds total.
 */
public class CommentAnimation extends Div {

    public CommentAnimation(Runnable onComplete) {
        getElement().executeJs(buildAnimationJS());
        scheduleDismiss(onComplete);
    }

    private String buildAnimationJS() {
        return
        // Overlay
        "var ov=document.createElement('div');ov.id='cmt-ov';" +
        "ov.style.cssText='position:fixed;inset:0;z-index:99999;pointer-events:none;display:flex;align-items:center;justify-content:center;flex-direction:column;background:rgba(30,30,47,.8);opacity:0;transition:opacity .25s ease;';" +
        "document.body.appendChild(ov);" +
        "requestAnimationFrame(function(){ov.style.opacity='1';});" +
        "\n" +
        // Speech bubble
        "var bubble=document.createElement('div');" +
        "bubble.style.cssText='position:relative;background:#F8F9FC;border-radius:16px 16px 16px 4px;padding:20px 28px;box-shadow:0 8px 32px rgba(108,92,231,.2);transform:scale(0);opacity:0;transition:all .35s cubic-bezier(.34,1.56,.64,1);min-width:180px;';" +
        // Lines inside bubble (writing effect)
        "for(var i=0;i<3;i++){" +
        "  var line=document.createElement('div');" +
        "  var widths=['80%','60%','40%'];" +
        "  line.style.cssText='height:10px;background:#E8EAED;border-radius:5px;margin:7px 0;opacity:0;transition:opacity .2s ease '+(.3+i*.15)+'s;width:'+widths[i]+';';" +
        "  bubble.appendChild(line);" +
        "}" +
        "ov.appendChild(bubble);" +
        "\n" +
        // Text below bubble
        "var txt=document.createElement('div');" +
        "txt.textContent='COMMENT POSTED';" +
        "txt.style.cssText='font-family:Courier New,monospace;font-size:clamp(.8rem,2.5vw,1.1rem);font-weight:700;letter-spacing:.1em;color:#A29BFE;text-shadow:0 0 12px rgba(108,92,231,.3);margin-top:16px;opacity:0;transition:opacity .25s ease .5s;white-space:nowrap;';" +
        "ov.appendChild(txt);" +
        "\n" +
        // Animate in
        "setTimeout(function(){" +
        "  bubble.style.transform='scale(1)';bubble.style.opacity='1';" +
        "  bubble.querySelectorAll('div').forEach(function(l){l.style.opacity='1';});" +
        "},150);" +
        "setTimeout(function(){txt.style.opacity='1';},500);";
    }

    private void scheduleDismiss(Runnable onComplete) {
        getElement().executeJs(
            "setTimeout(function(){" +
            "  var ov=document.getElementById('cmt-ov');" +
            "  if(ov){ov.style.opacity='0';setTimeout(function(){ov.remove();},350);}" +
            "},1400);"
        );

        UI ui = UI.getCurrent();
        if (ui != null) {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try { Thread.sleep(1800); } catch (InterruptedException ignored) {}
                ui.access(onComplete::run);
            });
        }
    }
}
