import React from 'react';
import ReactStars from 'react-stars';

interface Props {
    rating: number;
    totalStars?: number;
}

const Rating: React.FC<Props> = ({ rating, totalStars = 5 }) => {
    let fullStars: number;
    let halfStar: number = 0;

    if (rating === totalStars) {
        fullStars = totalStars;
    } else if (rating >= totalStars - 0.5) {
        fullStars = totalStars - 1;
        halfStar = 1;
    } else {
        fullStars = Math.floor(rating);
        halfStar = rating % 1 >= 0.5 ? 1 : 0;
    }

    const renderStars = () => {
        const stars = [];
        for (let i = 0; i < fullStars; i++) {
            stars.push(<ReactStars key={`star-${i}`} count={1} value={1} size={30} color2={"#fff400"} edit={false} />);
        }
        if (halfStar) {
            stars.push(<ReactStars key="half-star" count={1} value={0.5} size={30} color2={"#fff400"} edit={false} />);
        }
        for (let i = fullStars + halfStar; i < totalStars; i++) {
            stars.push(<ReactStars key={`empty-star-${i}`} count={1} value={0} size={30} color2={"#ffffff"} edit={false} />);
        }
        return stars;
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
            {renderStars()}
        </div>
    );
};

export default Rating;
