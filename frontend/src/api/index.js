import axios from 'axios';

const apiClient = axios.create({
    baseURL: '/api', // 后端 API 基础路径
    headers: {
        'Content-Type': 'application/json',
    },
});

export const getPrizesByActivityId = (activityId) => {
    return apiClient.get(`/activity-prizes/${activityId}`);
};

export const updatePrizes = (activityId, updates) => {
    return apiClient.put(`/activity-prizes/${activityId}/update`, updates);
};