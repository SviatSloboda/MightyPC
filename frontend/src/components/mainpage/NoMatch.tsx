import React from 'react';

const NoMatch: React.FC = () => {
    return (
        <div className="no-match">
            <h1 className="no-match__title">Page Not Found</h1>
            <p className="no-match__text">Sorry, the page you are looking for does not exist.</p>
            <a href="/" className="no-match__link">Go back to the homepage</a>
        </div>
    );
};

export default NoMatch;
