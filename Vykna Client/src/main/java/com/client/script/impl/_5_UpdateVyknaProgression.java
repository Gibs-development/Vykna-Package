package com.client.script.impl;

import com.client.graphics.interfaces.impl.AchievementHomePage;
import com.client.graphics.interfaces.impl.AchievementListPage;
import com.client.utilities.JsonUtil;
import com.client.vykna_progression.ProgressionListPayload;
import com.client.vykna_progression.ProgressionListTypePayload;
import com.client.vykna_progression.ProgressionSummaryPayload;
import com.client.vykna_progression.VyknaProgressionDefinitions;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

public class _5_UpdateVyknaProgression {

    public static void handle(String type, String data) throws IOException {
        if ("listTypes".equalsIgnoreCase(type)) {
            List<ProgressionListTypePayload> payload = JsonUtil.fromJsonString(
                    data, new TypeToken<List<ProgressionListTypePayload>>() {});
            VyknaProgressionDefinitions.setListTypes(payload);
            return;
        }

        if ("listData".equalsIgnoreCase(type)) {
            ProgressionListPayload payload = JsonUtil.fromJsonString(
                    data, new TypeToken<ProgressionListPayload>() {});
            VyknaProgressionDefinitions.applyListPayload(payload);
            AchievementListPage.applyServerPayload(payload.getListTypeId(), payload.getPageIndex());
            AchievementHomePage.refreshProgressions();
            return;
        }

        if ("summaryData".equalsIgnoreCase(type)) {
            ProgressionSummaryPayload payload = JsonUtil.fromJsonString(
                    data, new TypeToken<ProgressionSummaryPayload>() {});
            VyknaProgressionDefinitions.setSummaryPayload(payload);
            AchievementListPage.setShowCompleted(payload.isShowCompleted());
            AchievementHomePage.refreshProgressions();
            return;
        }

        if ("toggleCompleted".equalsIgnoreCase(type)) {
            boolean showCompleted = "1".equals(data) || "true".equalsIgnoreCase(data);
            AchievementListPage.setShowCompleted(showCompleted);
            return;
        }
    }
}
