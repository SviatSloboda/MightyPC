import {ChangeEvent} from "react";

type PhotoProps = {
    savePhoto: (file: File) => void
}

export default function Photo(props: Readonly<PhotoProps>) {

    function savePhoto(event: ChangeEvent<HTMLInputElement>) {
        if (event.target.files && event.target.files.length > 0) {
            const file = event.target.files[0];
            props.savePhoto(file);
        }
    }

    return (<label htmlFor="selfie" className="upload-button"> Upload Photo<input
        type="file"
        id="selfie"
        accept="image/png, image/jpeg"
        capture="user"
        onChange={savePhoto}
        className="upload-input"/>
    </label>)
}
