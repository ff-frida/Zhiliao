package com.shatyuka.zhiliao;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Functions {
    final static boolean DEBUG_WEBVIEW = false;

    static boolean horizontal = false;

    static boolean init(final ClassLoader classLoader) {
        try {
            XposedBridge.hookMethod(Helper.isShowLaunchAd, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_launchad", true))
                        param.setResult(false);
                }
            });
            XposedHelpers.findAndHookMethod(Helper.AdNetworkManager, "a", int.class, long.class, long.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_launchad", true)) {
                        param.setResult("");
                    }
                }
            });

            XposedBridge.hookAllMethods(Helper.InnerDeserializer, "deserialize", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false)) {
                        Object result = param.getResult();
                        if (result == null)
                            return;
                        Class<?> resultClass = result.getClass();
                        if (result != null) {
                            if (resultClass == Helper.ApiTemplateRoot) {
                                String type = (String) Helper.DataUnique_type.get(Helper.ApiTemplateRoot_extra.get(result));
                                if (Helper.prefs.getBoolean("switch_video", false) && (type.equals("zvideo") || type.equals("drama"))) {
                                    param.setResult(null);
                                } else {
                                    if (Helper.regex_title == null && Helper.regex_author == null && Helper.regex_content == null)
                                        return;
                                    Object feed_content = Helper.ApiFeedCard_feed_content.get(Helper.ApiTemplateRoot_common_card.get(result));
                                    if (feed_content == null)
                                        return;
                                    if (Helper.regex_title != null) {
                                        String title = (String) Helper.ApiText_panel_text.get(Helper.ApiFeedContent_title.get(feed_content));
                                        if (Helper.regex_title.matcher(title).find()) {
                                            param.setResult(null);
                                        }
                                    }
                                    if (Helper.regex_author != null) {
                                        Object sourceLine = Helper.ApiFeedContent_sourceLine.get(feed_content);
                                        List elements = (List) Helper.ApiLine_elements.get(sourceLine);
                                        String author = (String) Helper.ApiText_panel_text.get(Helper.ApiElement_text.get(elements.get(1)));
                                        if (Helper.regex_author.matcher(author).find()) {
                                            param.setResult(null);
                                        }
                                    }
                                    if (Helper.regex_content != null) {
                                        String content = (String) Helper.ApiText_panel_text.get(Helper.ApiFeedContent_content.get(feed_content));
                                        if (Helper.regex_content.matcher(content).find()) {
                                            param.setResult(null);
                                        }
                                    }
                                }
                            } else if (resultClass == Helper.MarketCard) {
                                if (Helper.prefs.getBoolean("switch_marketcard", false)) {
                                    param.setResult(null);
                                }
                            }
                        }
                    }
                }
            });

            XposedHelpers.findAndHookMethod(Helper.MorphAdHelper, "resolve", Context.class, Helper.FeedAdvert, boolean.class, Boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_feedad", true)) {
                        param.setResult(false);
                    }
                }
            });
            XposedHelpers.findAndHookMethod(Helper.Advert, "isSlidingWindow", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_feedad", true)) {
                        param.setResult(false);
                    }
                }
            });
            XposedHelpers.findAndHookMethod(Helper.Ad, "isFloatAdCard", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_feedad", true)) {
                        param.setResult(false);
                    }
                }
            });

            XposedHelpers.findAndHookMethod(Helper.MorphAdHelper, "resolveAnswerAdParam", Context.class, "com.zhihu.android.api.model.AnswerListAd", Boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_answerlistad", true)) {
                        param.setResult(false);
                    }
                }
            });
            XposedHelpers.findAndHookMethod(Helper.AnswerListWrapper, "insertAdBrandToList", ArrayList.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_answerlistad", true)) {
                        param.setResult(null);
                    }
                }
            });

            XposedHelpers.findAndHookMethod(Helper.MorphAdHelper, "resolveCommentAdParam", Context.class, "com.zhihu.android.api.model.CommentListAd", Boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_commentad", true)) {
                        param.setResult(false);
                    }
                }
            });

            XposedBridge.hookMethod(Helper.shouldInterceptRequest, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (!Helper.prefs.getBoolean("switch_mainswitch", false))
                        return;
                    WebResourceRequest request = (WebResourceRequest) param.args[1];
                    List<String> segments = request.getUrl().getPathSegments();
                    if (segments.size() > 2 && request.getMethod().equals("GET")
                            && ((Helper.prefs.getBoolean("switch_answerad", true) && (segments.get(segments.size() - 1).equals("recommendations") || (segments.get(2).equals("brand") && segments.get(segments.size() - 1).equals("card"))))
                            || (Helper.prefs.getBoolean("switch_club", false) && segments.get(segments.size() - 1).equals("bind_club"))
                            || (Helper.prefs.getBoolean("switch_goods", false) && segments.get(segments.size() - 2).equals("goods")))) {
                        WebResourceResponse response = new WebResourceResponse("application/json", "UTF-8", new ByteArrayInputStream("null\n".getBytes()));
                        response.setStatusCodeAndReasonPhrase(200, "OK");
                        param.setResult(response);
                    }
                }
            });

            XposedBridge.hookMethod(Helper.showShareAd, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_sharead", true))
                        param.setResult(null);
                }
            });

            XposedBridge.hookAllConstructors(Helper.FeedsTabsTopEntranceManager, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_livebutton", false)) {
                        param.args[0] = new FrameLayout(((FrameLayout) param.args[0]).getContext());
                    }
                }
            });

            XposedHelpers.findAndHookMethod(java.io.File.class, "exists", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    File file = (File) param.thisObject;
                    if (file.getName().equals(".allowXposed")) {
                        param.setResult(true);
                    }
                }
            });

            if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_horizontal", false)) {
                XposedHelpers.findAndHookMethod(Helper.ActionSheetLayout, "onTouchEvent", MotionEvent.class, new XC_MethodHook() {
                    float old_x = 0;
                    float old_y = 0;

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        MotionEvent e = (MotionEvent) param.args[0];
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                old_x = e.getX();
                                old_y = e.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                float dx = e.getX() - old_x;
                                float dy = e.getY() - old_y;
                                if (Math.abs(dx) > Helper.width && Math.abs(dy) < Helper.height) {
                                    for (Object callback : (List) Helper.ActionSheetLayout_callbackList.get(param.thisObject)) {
                                        if (callback.getClass() == Helper.NestChildScrollChange) {
                                            Helper.onNestChildScrollRelease.invoke(callback, dx, 5201314);
                                        }
                                    }
                                }
                                break;
                        }
                    }
                });
                XposedHelpers.findAndHookMethod(Helper.VerticalPageTransformer, "transformPage", View.class, float.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        View view = (View) param.args[0];
                        float position = (float) param.args[1];
                        if (position < -1) {
                            view.setAlpha(0);
                        } else if (position <= 1) {
                            view.setAlpha(1);
                            view.setTranslationX(horizontal ? 0 : view.getWidth() * -position);
                            view.setTranslationY(horizontal ? 0 : view.getHeight() * position);
                        } else {
                            view.setAlpha(0);
                        }
                        return null;
                    }
                });
                XposedHelpers.findAndHookMethod(Helper.NestChildScrollChange, "onNestChildScrollRelease", float.class, int.class, new XC_MethodHook() {
                    XC_MethodHook.Unhook hook_isReadyPageTurning;

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if ((int) param.args[1] == 5201314) {
                            hook_isReadyPageTurning = XposedBridge.hookMethod(Helper.isReadyPageTurning, XC_MethodReplacement.returnConstant(true));
                            horizontal = true;
                        } else {
                            horizontal = false;
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (hook_isReadyPageTurning != null) {
                            hook_isReadyPageTurning.unhook();
                        }
                    }
                });
                XposedHelpers.findAndHookMethod(Helper.NextBtnClickListener, "onClick", View.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        horizontal = false;
                    }
                });
                XposedHelpers.findAndHookMethod(Helper.AnswerContentView, "showNextAnswer", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        horizontal = false;
                    }
                });
            }

            if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_nextanswer", false)) {
                XposedHelpers.findAndHookMethod(Helper.AnswerPagerFragment, "setupNextAnswerBtn", XC_MethodReplacement.returnConstant(null));
            }

            if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_reddot", false)) {
                XposedBridge.hookAllMethods(Helper.FeedsTabsFragment, "onUnReadCountLoaded", XC_MethodReplacement.returnConstant(null));
                XposedBridge.hookAllMethods(Helper.FeedFollowAvatarCommonViewHolder, "b", XC_MethodReplacement.returnConstant(null));
                XposedHelpers.findAndHookMethod(Helper.ZHMainTabLayout, "d", XC_MethodReplacement.returnConstant(null));
                XposedHelpers.findAndHookMethod(Helper.BottomNavMenuItemView, "a", int.class, XC_MethodReplacement.returnConstant(null));
                XposedHelpers.findAndHookMethod(Helper.BottomNavMenuItemViewForIconOnly, "a", int.class, XC_MethodReplacement.returnConstant(null));
                XposedHelpers.findAndHookMethod(Helper.NotiUnreadCountKt, "hasUnread", int.class, XC_MethodReplacement.returnConstant(false));
                XposedHelpers.findAndHookMethod(Helper.NotiMsgModel, "getUnreadCount", XC_MethodReplacement.returnConstant(0));
            }

            XposedHelpers.findAndHookMethod(Helper.LinkZhihuHelper, "a", "com.zhihu.android.app.mercury.api.c", "com.zhihu.android.app.mercury.api.IZhihuWebView", String.class, new XC_MethodHook() {
                XC_MethodHook.Unhook hook_isLinkZhihu;

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && (Helper.prefs.getBoolean("switch_externlink", false) || Helper.prefs.getBoolean("switch_externlinkex", false))) {
                        String url = (String) param.args[2];
                        if (url.startsWith("https://link.zhihu.com/?target=")) {
                            param.args[2] = URLDecoder.decode(url.substring(31), "utf-8");
                            if (Helper.prefs.getBoolean("switch_externlinkex", false)) {
                                android.util.Log.d("Zhiliao", (String) param.args[2]);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) param.args[2]));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Helper.context.startActivity(intent);
                                param.setResult(true);
                            } else {
                                hook_isLinkZhihu = XposedBridge.hookMethod(Helper.isLinkZhihu, XC_MethodReplacement.returnConstant(true));
                            }
                        }
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    if (hook_isLinkZhihu != null)
                        hook_isLinkZhihu.unhook();
                }
            });

            if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_vipbanner", false)) {
                XposedHelpers.findAndHookMethod(Helper.VipEntranceView, "a", Context.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        XmlResourceParser layout_vipentranceview = Helper.modRes.getLayout(R.layout.layout_vipentranceview);
                        LayoutInflater.from((Context) param.args[0]).inflate(layout_vipentranceview, (ViewGroup) param.thisObject);
                        return null;
                    }
                });
                XposedHelpers.findAndHookMethod(Helper.VipEntranceView, "setData", "com.zhihu.android.api.model.VipInfo", XC_MethodReplacement.returnConstant(null));
                XposedHelpers.findAndHookMethod(Helper.VipEntranceView, "onClick", View.class, XC_MethodReplacement.returnConstant(null));
                XposedHelpers.findAndHookMethod(Helper.VipEntranceView, "resetStyle", XC_MethodReplacement.returnConstant(null));
            }

            if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_vipnav", false)) {
                XposedHelpers.findAndHookMethod(Helper.BottomNavMenuView, "a", Helper.IMenuItem, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if ("market".equals(Helper.getMenuName.invoke(param.args[0]))) {
                            ((View) Helper.Tab_tabView.get(param.getResult())).setVisibility(View.GONE);
                        }
                    }
                });
            }

            XposedBridge.hookAllMethods(Helper.InternalNotificationManager, "fetchFloatNotification", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_hotbanner", false)) {
                        param.setResult(null);
                    }
                }
            });

            if (Build.VERSION.SDK_INT >= 26) {
                XposedHelpers.findAndHookMethod(Window.class, "setColorMode", int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (Helper.prefs.getBoolean("switch_mainswitch", false) && Helper.prefs.getBoolean("switch_colormode", false)) {
                            param.setResult(null);
                        }
                    }
                });
            }

            if (DEBUG_WEBVIEW) {
                XposedBridge.hookAllConstructors(WebView.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.callStaticMethod(WebView.class, "setWebContentsDebuggingEnabled", true);
                    }
                });
            }

            return true;
        } catch (Exception e) {
            XposedBridge.log("[Zhiliao] " + e.toString());
            return false;
        }
    }
}
