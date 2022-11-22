import request from "../utils/request";

export function uploadFiles(formData: FormData, progress: Function) {
    return request({
        url: `/pcap/upload`,
        method: 'post',
        data: formData,
        onUploadProgress: progressEvent => {
            let {total, loaded} = progressEvent
            let percent: number = loaded / total * 100;
            progress(Number.parseFloat(percent.toFixed(2)));
        }
    })
}
